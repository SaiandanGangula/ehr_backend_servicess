package sirobilt.meghasanjivini.abhaservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import sirobilt.meghasanjivini.abhaservice.client.AbdmSessionClient
import sirobilt.meghasanjivini.abhaservice.client.AbdmPublicKeyClient
import sirobilt.meghasanjivini.abhaservice.client.AbdmProfileClient
import sirobilt.meghasanjivini.abhaservice.dto.SessionRequest
import sirobilt.meghasanjivini.abhaservice.dto.SearchAbhaRequest
import sirobilt.meghasanjivini.abhaservice.dto.SearchAbhaResponseDTO
import sirobilt.meghasanjivini.abhaservice.util.RsaEncryptor
import java.time.Instant
import java.util.UUID

@ApplicationScoped
class AbhaSearchService {

    private val logger = Logger.getLogger(AbhaSearchService::class.java)

    @ConfigProperty(name = "abdm.client-id")
    lateinit var clientId: String

    @ConfigProperty(name = "abdm.client-secret")
    lateinit var clientSecret: String

    @ConfigProperty(name = "abdm.environment")
    lateinit var environment: String

    @ConfigProperty(name = "abdm.benefit-name")
    lateinit var benefitName: String

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
    lateinit var profileClient: AbdmProfileClient

    /**
     * Initiates ABHA search flow using mobile number.
     * Uses injected ObjectMapper for better performance.
     */
    fun initiateAbhaSearchFlow(plainMobile: String): SearchAbhaResponseDTO {
        logger.info("Initiating ABHA search flow for mobile number")

        try {
            val requestId = UUID.randomUUID().toString()
            val timestamp = Instant.now().toString()
            val cmId = if (environment == "sandbox") "sbx" else "abdm"

            // Step 1: Generate Session Token
            val sessionReq = SessionRequest(clientId, clientSecret, "client_credentials")
            val sessionResp = sessionClient.createSession(
                sessionReq,
                requestId,
                timestamp,
                cmId,
                "application/json"
            )
            if (sessionResp.status !in listOf(200, 202)) {
                throw AbhaSearchException("Failed to get session token: HTTP ${sessionResp.status}", sessionResp.status)
            }
            val sessionMap = sessionResp.readEntity(Map::class.java)
            val accessToken = sessionMap["accessToken"] as String

            // Step 2: Fetch Public Key
            val pubKeyResp = publicKeyClient.getPublicKey(
                requestId,
                timestamp,
                cmId,
                "Bearer $accessToken"
            )
            if (pubKeyResp.status != 200) {
                throw AbhaSearchException("Failed to get public key: HTTP ${pubKeyResp.status}", pubKeyResp.status)
            }
            val pubKeyMap = pubKeyResp.readEntity(Map::class.java)
            val publicKeyPem = pubKeyMap["publicKey"] as String

            // Step 3: Encrypt Mobile Number
            val encryptedMobile = RsaEncryptor.encrypt(plainMobile, publicKeyPem)

            // Step 4: Build and call search request
            val searchReq = SearchAbhaRequest(
                scope = listOf("search-abha"),
                mobile = encryptedMobile
            )

            logger.debug("Sending ABHA search request to ABDM")

            // Step 5: Call Search API
            val searchRaw = profileClient.searchAbha(
                searchReq,
                requestId,
                timestamp,
                "Bearer $accessToken",
                benefitName
            )
            if (searchRaw.status != 200) {
                throw AbhaSearchException("ABHA search failed: HTTP ${searchRaw.status}", searchRaw.status)
            }

            // Parse and return
            val result = searchRaw.readEntity(SearchAbhaResponseDTO::class.java)
            logger.info("ABHA search completed successfully")
            return result

        } catch (e: Exception) {
            logger.error("Failed to complete ABHA search", e)
            when (e) {
                is AbhaSearchException -> throw e
                else -> throw AbhaSearchException("Unexpected error during ABHA search", 500, e)
            }
        }
    }
}

/**
 * Custom exception for ABHA search-related errors
 */
class AbhaSearchException(
    message: String,
    val statusCode: Int = 500,
    cause: Throwable? = null
) : RuntimeException(message, cause)

