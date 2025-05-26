package sirobilt.meghasanjivini.abhaservice.dto

class PublicKeyResponse {
    val algorithm: String
    val publicKey: String

    constructor(algorithm: String, publicKey: String) {
        this.algorithm = algorithm
        this.publicKey = publicKey
    }
}
