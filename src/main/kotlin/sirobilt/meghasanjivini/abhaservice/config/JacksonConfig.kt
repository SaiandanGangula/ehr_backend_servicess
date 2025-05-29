package sirobilt.meghasanjivini.abhaservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import jakarta.inject.Singleton

/**
 * Jackson ObjectMapper configuration for optimal performance.
 * Creates a singleton ObjectMapper instance to avoid recreation overhead.
 */
@ApplicationScoped
class JacksonConfig {

    @Produces
    @Singleton
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            // Register Kotlin module for proper Kotlin support
            registerModule(KotlinModule.Builder().build())
            
            // Register Java Time module for Instant, LocalDateTime, etc.
            registerModule(JavaTimeModule())
            
            // Keep default camelCase for property naming to match existing DTOs
            // propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
            
            // Configure to ignore unknown properties for resilience
            configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            
            // Configure to not fail on empty beans
            configure(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            
            // Write dates as ISO strings instead of timestamps
            configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        }
    }
}
