package sirobilt.meghasanjivini.abhaservice.controller

import EnrollByOtpRequest
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import sirobilt.meghasanjivini.abhaservice.service.AbhaEnrollService


@Path("/abha/enroll")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class AbhaEnrollController @Inject constructor(
    private val abhaEnrollService: AbhaEnrollService
) {
    @POST
    @Path("/by-otp")
    fun enrollByOtp(request: EnrollByOtpRequest): Response {
        val result = abhaEnrollService.enrollAbhaWithAadhaarOtp(
            txnId = request.txnId,
            otp = request.otp,
            mobile = request.mobile
        )
        return Response.ok(result).build()
    }
}
