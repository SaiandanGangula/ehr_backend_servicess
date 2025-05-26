package sirobilt.meghasanjivini.abhaservice.dto

import com.fasterxml.jackson.annotation.JsonProperty


class SessionRequest {
    @JsonProperty("clientId")
    var clientId: String
    @JsonProperty("clientSecret")
    var clientSecret: String
    @JsonProperty("grantType")
    var grantType: String

    constructor(clientId: String, clientSecret: String, grantType: String = "client_credentials") {
        this.clientId = clientId
        this.clientSecret = clientSecret
        this.grantType = grantType
    }
}





data class OtpRequest(
    @JsonProperty("txnId")
    var txnId: String? = null,

    @JsonProperty("scope")
    var scope: List<String>? = null,

    @JsonProperty("loginHint")
    var loginHint: String? = null,

    @JsonProperty("loginId")
    var loginId: String? = null,

    @JsonProperty("otpSystem")
    var otpSystem: String? = null
)
