package sirobilt.meghasanjivini.abhaservice.client


import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import sirobilt.meghasanjivini.abhaservice.dto.OtpRequest

@RegisterRestClient(configKey = "abdm-otp")
interface AbdmOtpClient {
    @POST
    @Path("/enrollment/request/otp")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun requestOtp(body: OtpRequest,
                   @HeaderParam("REQUEST-ID") requestId: String,
                   @HeaderParam("TIMESTAMP") timestamp: String,
                   @HeaderParam("X-CM-ID") cmId: String,
                   @HeaderParam("Authorization") token: String): Response
}