package sirobilt.meghasanjivini.abhaservice.controller

import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.jboss.logging.Logger
import sirobilt.meghasanjivini.abhaservice.dto.AbhaAadhaarOtpResponseDTO
import sirobilt.meghasanjivini.abhaservice.dto.AbhaAadhaarRequestDTO
import sirobilt.meghasanjivini.abhaservice.service.AbhaAadhaarService

@Path("/abha")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class AbhaAadhaarController {

    private val logger = Logger.getLogger(AbhaAadhaarController::class.java)

    @Inject
    lateinit var abhaAadhaarService: AbhaAadhaarService

    @POST
    @Path("/aadhaar-otp")
    fun abhaOtpFlow(request: AbhaAadhaarRequestDTO?): AbhaAadhaarOtpResponseDTO {
        logger.info("Received ABHA OTP request")

        // Validate request
        if (request == null) {
            logger.error("Request body is null")
            throw BadRequestException("Request body is required")
        }

        if (request.aadhaarNumber.isNullOrBlank()) {
            logger.error("Aadhaar number is null or blank")
            throw BadRequestException("Aadhaar number is required")
        }

        logger.info("Processing ABHA OTP request for Aadhaar")
        return abhaAadhaarService.initiateAbhaOtpFlow(request.aadhaarNumber!!)
    }
}