import com.fasterxml.jackson.annotation.JsonInclude


@JsonInclude(JsonInclude.Include.NON_NULL)
data class OtpAuthData(
    val txnId: String,
    val otpValue: String,
    val mobile: String? = null
)

// AuthData.kt
data class AuthData(
    val authMethods: List<String> = listOf("otp"),
    val otp: OtpAuthData
)

// ConsentData.kt
data class ConsentData(
    val code: String = "abha-enrollment",
    val version: String = "1.4"
)

// AbhaEnrollmentRequest.kt
data class AbhaEnrollmentRequest(
    val authData: AuthData,
    val consent: ConsentData = ConsentData()
)

data class EnrollByOtpRequest(
    val txnId: String,
    val otp: String,
    val mobile: String? = null
)

