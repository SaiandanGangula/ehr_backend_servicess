package sirobilt.meghasanjivini.abhaservice.model


import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "abha_registrations")
data class AbhaRegistration(
    @Id @GeneratedValue var id: Long? = null,
    var sessionToken: String = "",
    var txnIdAadhaar: String? = null,
    var txnIdMobile: String? = null,
    var abhaNumber: String? = null,
    var createdAt: Instant = Instant.now()
)