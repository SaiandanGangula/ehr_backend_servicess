package sirobilt.meghasanjivini.abhaservice.controller

import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import sirobilt.meghasanjivini.abhaservice.dto.AbhaAadhaarOtpResponseDTO
import sirobilt.meghasanjivini.abhaservice.dto.AbhaAadhaarRequestDTO
import sirobilt.meghasanjivini.abhaservice.service.AbhaAadhaarService

@Path("/abha")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class AbhaAadhaarController {

    @Inject
    lateinit var abhaAadhaarService: AbhaAadhaarService

    @POST
    @Path("/aadhaar-otp")
    fun abhaOtpFlow(request: AbhaAadhaarRequestDTO): AbhaAadhaarOtpResponseDTO  {
        val aadhaar = request.aadhaarNumber ?: throw BadRequestException("Aadhaar number required")
        return abhaAadhaarService.initiateAbhaOtpFlow(aadhaar)
    }
}