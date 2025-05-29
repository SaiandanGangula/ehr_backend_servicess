package sirobilt.meghasanjivini.abhaservice.controller

import jakarta.inject.Inject
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import sirobilt.meghasanjivini.abhaservice.dto.PlainMobileRequestDTO
import sirobilt.meghasanjivini.abhaservice.dto.SearchAbhaResponseDTO
import sirobilt.meghasanjivini.abhaservice.service.AbhaSearchService

@Path("/abha")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class AbhaSearchController {

    @Inject
    lateinit var abhaSearchService: AbhaSearchService

    @POST
    @Path("/search")
    fun searchAbha(request: PlainMobileRequestDTO): SearchAbhaResponseDTO {
        val mobile = request.mobile
            ?: throw BadRequestException("Mobile number is required")
        return abhaSearchService.initiateAbhaSearchFlow(mobile)
    }
}