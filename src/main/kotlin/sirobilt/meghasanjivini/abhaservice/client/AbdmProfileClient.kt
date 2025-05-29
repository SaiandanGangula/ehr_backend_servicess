package sirobilt.meghasanjivini.abhaservice.client



import sirobilt.meghasanjivini.abhaservice.dto.SearchAbhaRequest
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@RegisterRestClient(configKey = "abdm-profile")
@Path("/v3/profile/account/abha")
interface AbdmProfileClient {

    @POST
    @Path("/search")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun searchAbha(
        body: SearchAbhaRequest,
        @HeaderParam("REQUEST-ID") requestId: String,
        @HeaderParam("TIMESTAMP") timestamp: String,
        @HeaderParam("Authorization") token: String,
        @HeaderParam("BENEFIT-NAME") benefitName: String
    ): Response
}
