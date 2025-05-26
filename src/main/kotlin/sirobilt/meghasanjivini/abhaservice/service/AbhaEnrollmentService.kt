package sirobilt.meghasanjivini.abhaservice.service
import AbhaEnrollmentRequest
import AuthData
import OtpAuthData
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import sirobilt.meghasanjivini.abhaservice.client.AbdmEnrollmentClient
import sirobilt.meghasanjivini.abhaservice.client.AbdmPublicKeyClient
import sirobilt.meghasanjivini.abhaservice.client.AbdmSessionClient
import sirobilt.meghasanjivini.abhaservice.dto.*
import sirobilt.meghasanjivini.abhaservice.util.RsaEncryptor
import java.time.Instant
import java.util.*

@ApplicationScoped
class AbhaEnrollService {

    @ConfigProperty(name = "abdm.client-id")
    lateinit var clientId: String

    @ConfigProperty(name = "abdm.client-secret")
    lateinit var clientSecret: String

    @ConfigProperty(name = "abdm.environment")
    lateinit var environment: String

    @Inject
    @RestClient
    lateinit var enrollmentClient: AbdmEnrollmentClient

    @Inject
    @RestClient
    lateinit var publicKeyClient: AbdmPublicKeyClient

    @Inject
    @RestClient
    lateinit var sessionClient: AbdmSessionClient

    fun enrollAbhaWithAadhaarOtp(
        txnId: String,
        otp: String,
        mobile: String? = null
    ): Map<String, Any> {
        val mapper = ObjectMapper()
        val requestId = UUID.randomUUID().toString()
        val timestamp = Instant.now().toString()
        val cmId = if (environment == "sandbox") "sbx" else "abdm"


        // 1. Get a fresh accessToken
        val sessionBody = SessionRequest(clientId, clientSecret, "client_credentials")
        val sessionResp = sessionClient.createSession(sessionBody, requestId, timestamp, cmId, "application/json")
        if (sessionResp.status != 200 && sessionResp.status != 202) throw RuntimeException("Failed to get session token: ${sessionResp.status}")
        val sessionJson = sessionResp.readEntity(Map::class.java)
        val accessToken = sessionJson["accessToken"] as String

        // 2. Get Public Key
        val pubKeyResp = publicKeyClient.getPublicKey(requestId, timestamp, cmId, "Bearer $accessToken")
        if (pubKeyResp.status != 200) throw RuntimeException("Failed to get public key: ${pubKeyResp.status}")
        val pubKeyMap = pubKeyResp.readEntity(Map::class.java)
        val publicKey = pubKeyMap["publicKey"] as String

        // 3. Encrypt the OTP
        val encryptedOtp = RsaEncryptor.encrypt(otp, publicKey)

        val otpData = if (mobile.isNullOrBlank()) {
            OtpAuthData(txnId, encryptedOtp, null) // will be omitted in JSON
        } else {
            OtpAuthData(txnId, encryptedOtp, mobile)
        }

        // 4. Build enrollment request
        val authData = AuthData(
            authMethods = listOf("otp"),
            otp = otpData
        )
        val request = AbhaEnrollmentRequest(authData = authData)
        println("ABHA ENROLLMENT REQUEST BODY: ${mapper.writeValueAsString(request)}")

        // 5. Call the enroll API
        val response = enrollmentClient.enrollAbha(
            body = request,
            requestId = requestId,
            timestamp = timestamp,
            token = "Bearer $accessToken"
        )
        val respBody = response.readEntity(Map::class.java) as Map<String, Any>
        println("ABHA ENROLLMENT RESPONSE: $respBody")

        if (response.status != 200)
            throw RuntimeException("Enrollment failed: HTTP ${response.status} - $respBody")

        return respBody
    }
}