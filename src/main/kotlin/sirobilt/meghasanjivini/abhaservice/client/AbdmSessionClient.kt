package sirobilt.meghasanjivini.abhaservice.client


import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import sirobilt.meghasanjivini.abhaservice.dto.SessionRequest

@RegisterRestClient(configKey = "abdm-session")
interface AbdmSessionClient {
    @POST
    @Path("/sessions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)

    fun createSession(body: SessionRequest,
                      @HeaderParam("REQUEST-ID") requestId: String,
                      @HeaderParam("TIMESTAMP") timestamp: String,
                      @HeaderParam("X-CM-ID") cmId: String,
                      @HeaderParam("Accept") accept: String ): Response
}