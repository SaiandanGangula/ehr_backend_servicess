package sirobilt.meghasanjivini.abhaservice.client


import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@RegisterRestClient(configKey = "abdm-publickey")
interface AbdmPublicKeyClient {
    @GET
    @Path("/public/certificate")
    fun getPublicKey(
        @HeaderParam("REQUEST-ID") requestId: String,
        @HeaderParam("TIMESTAMP") timestamp: String,
        @HeaderParam("X-CM-ID") cmId: String,
        @HeaderParam("Authorization") authorization: String
    ): Response
}