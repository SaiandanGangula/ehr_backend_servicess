package sirobilt.meghasanjivini.abhaservice.dto

import jakarta.ws.rs.QueryParam

data class SearchAbhaRequest(
    val scope: List<String>,
    val mobile: String
)

data class SearchAbhaResponseDTO(
    val profiles: List<SearchProfileDTO>
)

data class SearchProfileDTO(
    val enrolmentNumber: String,
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val dob: String?,
    val gender: String?,
    val mobile: String,
    val abhaStatus: String
)

data class PlainMobileRequestDTO(
    val mobile: String?
)