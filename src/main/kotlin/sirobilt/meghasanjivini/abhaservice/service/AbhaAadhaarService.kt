package sirobilt.meghasanjivini.abhaservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import sirobilt.meghasanjivini.abhaservice.client.AbdmOtpClient
import sirobilt.meghasanjivini.abhaservice.client.AbdmPublicKeyClient
import sirobilt.meghasanjivini.abhaservice.client.AbdmSessionClient
import sirobilt.meghasanjivini.abhaservice.dto.AbhaAadhaarOtpResponseDTO
import sirobilt.meghasanjivini.abhaservice.dto.OtpRequest
import sirobilt.meghasanjivini.abhaservice.dto.SessionRequest
import sirobilt.meghasanjivini.abhaservice.util.RsaEncryptor
import java.time.Instant
import java.util.*



@ApplicationScoped
class AbhaAadhaarService {

    private val logger = Logger.getLogger(AbhaAadhaarService::class.java)

    @ConfigProperty(name = "abdm.client-id")
    lateinit var clientId: String

    @ConfigProperty(name = "abdm.client-secret")
    lateinit var clientSecret: String

    @ConfigProperty(name = "abdm.environment")
    lateinit var environment: String

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Inject
    @RestClient
    lateinit var sessionClient: AbdmSessionClient

    @Inject
    @RestClient
    lateinit var publicKeyClient: AbdmPublicKeyClient

    @Inject
    @RestClient
    lateinit var otpClient: AbdmOtpClient

    /**
     * Initiates ABHA OTP flow using Aadhaar number.
     * Uses injected ObjectMapper for better performance.
     */
    fun initiateAbhaOtpFlow(aadhaar: String): AbhaAadhaarOtpResponseDTO {
        logger.info("Initiating ABHA OTP flow for Aadhaar")

        try {
            val requestId = UUID.randomUUID().toString()
            val timestamp = Instant.now().toString()
            val cmId = if (environment == "sandbox") "sbx" else "abdm"

            // Step 1: Generate Session Token
            val sessionBody = SessionRequest(clientId, clientSecret, "client_credentials")
            val sessionResp = sessionClient.createSession(sessionBody, requestId, timestamp, cmId, "application/json")

            if (sessionResp.status != 200 && sessionResp.status != 202) {
                throw AbhaOtpException("Failed to get session token: ${sessionResp.status}", sessionResp.status)
            }

            val sessionJson = sessionResp.readEntity(Map::class.java)
            val accessToken = sessionJson["accessToken"] as String

            // Step 2: Get Public Key
            val pubKeyResp = publicKeyClient.getPublicKey(requestId, timestamp, cmId, "Bearer $accessToken")
            if (pubKeyResp.status != 200) {
                throw AbhaOtpException("Failed to get public key: ${pubKeyResp.status}", pubKeyResp.status)
            }
            val pubKeyMap = pubKeyResp.readEntity(Map::class.java)
            val publicKey = pubKeyMap["publicKey"] as String

            // Step 3: Encrypt Aadhaar Number
            val encryptedAadhaar = RsaEncryptor.encrypt(aadhaar, publicKey)


            // Step 4: Create OTP request
            val otpRequest = OtpRequest(
                txnId = null,
                scope = listOf("abha-enrol"),
                loginHint = "aadhaar",
                loginId = encryptedAadhaar,
                otpSystem = "aadhaar"
            )

            // Step 5: Call OTP API (using injected ObjectMapper for performance)
            logger.debug("Sending OTP request to ABDM")
            val otpResponse = otpClient.requestOtp(
                otpRequest,
                requestId,
                timestamp,
                cmId,
                "Bearer $accessToken"
            )

            if (otpResponse.status != 200) {
                throw AbhaOtpException(
                    "Failed to request OTP: HTTP ${otpResponse.status}",
                    otpResponse.status
                )
            }

            val responseBody = otpResponse.readEntity(Map::class.java)

            // Step 6: Build and return response
            val result = AbhaAadhaarOtpResponseDTO().apply {
                txnId = responseBody["txnId"] as? String
                message = responseBody["message"] as? String ?: "OTP request initiated successfully"
            }

            logger.info("ABHA OTP flow initiated successfully")
            return result

        } catch (e: Exception) {
            logger.error("Failed to initiate ABHA OTP flow", e)
            when (e) {
                is AbhaOtpException -> throw e
                else -> throw AbhaOtpException("Unexpected error during OTP initiation", 500, e)
            }
        }
    }

}

/**
 * Custom exception for ABHA OTP-related errors
 */
class AbhaOtpException(
    message: String,
    val statusCode: Int = 500,
    cause: Throwable? = null
) : RuntimeException(message, cause)
