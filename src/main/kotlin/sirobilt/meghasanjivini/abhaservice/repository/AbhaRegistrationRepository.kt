package sirobilt.meghasanjivini.abhaservice.repository

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped
import sirobilt.meghasanjivini.abhaservice.model.AbhaRegistration

@ApplicationScoped
class AbhaRegistrationRepository : PanacheRepositoryBase<AbhaRegistration, Long>