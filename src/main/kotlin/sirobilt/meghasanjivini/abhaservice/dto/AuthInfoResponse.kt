package sirobilt.meghasanjivini.abhaservice.dto
import jakarta.json.bind.annotation.JsonbProperty
class AuthInfoResponse {
    val accessToken: String
    val expiresIn: Int
    val publicKey: String

    constructor(accessToken: String, expiresIn: Int, publicKey: String) {
        this.accessToken = accessToken
        this.expiresIn = expiresIn
        this.publicKey = publicKey
    }
}












class AbhaAadhaarRequestDTO {
    var aadhaarNumber: String? = null
}

class AbhaAadhaarOtpResponseDTO {
    var txnId: String? = null
    var message: String? = null
}