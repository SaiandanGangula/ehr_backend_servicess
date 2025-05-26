package sirobilt.meghasanjivini.abhaservice.service


import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
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

    @ConfigProperty(name = "abdm.client-id")
    lateinit var clientId: String

    @ConfigProperty(name = "abdm.client-secret")
    lateinit var clientSecret: String

    @ConfigProperty(name = "abdm.environment")
    lateinit var environment: String

    @Inject
    @RestClient
    lateinit var sessionClient: AbdmSessionClient

    @Inject
    @RestClient
    lateinit var publicKeyClient: AbdmPublicKeyClient

    @Inject
    @RestClient
    lateinit var otpClient: AbdmOtpClient

    fun initiateAbhaOtpFlow(aadhaar: String): AbhaAadhaarOtpResponseDTO  {

        val mapper = ObjectMapper()
        val requestId = UUID.randomUUID().toString()
        val timestamp = Instant.now().toString()
        val cmId = if (environment == "sandbox") "sbx" else "abdm"

        // Step 1: Generate Session Token
        val sessionBody = SessionRequest(clientId, clientSecret, "client_credentials")




        val sessionResp = sessionClient.createSession(sessionBody, requestId, timestamp, cmId,  "application/json")


        if (sessionResp.status != 200 && sessionResp.status != 202) throw RuntimeException("Failed to get session token: ${sessionResp.status}")

        val sessionJson = sessionResp.readEntity(Map::class.java)
        val accessToken = sessionJson["accessToken"] as String

        // Step 2: Get Public Key
        val pubKeyResp = publicKeyClient.getPublicKey(requestId,timestamp,cmId,"Bearer $accessToken")
        if (pubKeyResp.status != 200) throw RuntimeException("Failed to get public key: ${pubKeyResp.status}")
        val pubKeyMap = pubKeyResp.readEntity(Map::class.java)
        val publicKey = pubKeyMap["publicKey"] as String

        // Step 3: Encrypt Aadhaar Number
        val encryptedAadhaar = RsaEncryptor.encrypt(aadhaar, publicKey)


        val otpRequest = OtpRequest(
            txnId = null, // or null if optional
            scope = listOf("abha-enrol"),
            loginHint = "aadhaar",
            loginId = encryptedAadhaar, // your encrypted aadhaar string
            otpSystem = "aadhaar"
        )

        println("OTP REQUEST BODY: " + mapper.writeValueAsString(otpRequest))
        val otpResp = otpClient.requestOtp(
            otpRequest, requestId, timestamp, cmId, "Bearer $accessToken"
        )



        if (otpResp.status != 200)
            throw RuntimeException("Failed to request OTP: ${otpResp.status}")
        val otpJson = otpResp.readEntity(Map::class.java)

        // 5. Build response
        val response = AbhaAadhaarOtpResponseDTO()
        response.txnId = otpJson["txnId"] as? String
        response.message = otpJson["message"] as? String ?: "OTP request initiated"

        return response
    }
    }
