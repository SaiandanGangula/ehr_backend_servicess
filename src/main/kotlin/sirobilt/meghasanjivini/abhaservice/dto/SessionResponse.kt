package sirobilt.meghasanjivini.abhaservice.dto

class SessionResponse {
    val accessToken: String
    val tokenType: String
    val expiresIn: Long

    constructor(accessToken: String, tokenType: String, expiresIn: Long) {
        this.accessToken = accessToken
        this.tokenType = tokenType
        this.expiresIn = expiresIn
    }
}