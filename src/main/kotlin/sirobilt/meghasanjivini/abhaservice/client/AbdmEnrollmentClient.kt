package sirobilt.meghasanjivini.abhaservice.client

import AbhaEnrollmentRequest
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient


@RegisterRestClient(configKey = "abdm-enrollment")
interface AbdmEnrollmentClient {
    @POST
    @Path("/enrollment/enrol/byAadhaar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun enrollAbha(
        body: AbhaEnrollmentRequest,
        @HeaderParam("REQUEST-ID") requestId: String,
        @HeaderParam("TIMESTAMP") timestamp: String,
        @HeaderParam("Authorization") token: String
    ): Response
}
