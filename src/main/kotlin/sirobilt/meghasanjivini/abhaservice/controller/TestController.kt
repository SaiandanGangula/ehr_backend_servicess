package sirobilt.meghasanjivini.abhaservice.controller

import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import com.fasterxml.jackson.databind.ObjectMapper
import org.jboss.logging.Logger

@Path("/test")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class TestController {

    private val logger = Logger.getLogger(TestController::class.java)

    @Inject
    lateinit var objectMapper: ObjectMapper

    @GET
    @Path("/health")
    fun health(): Map<String, String> {
        logger.info("Health check called")
        return mapOf(
            "status" to "OK",
            "service" to "EHR ABHA Service",
            "objectMapper" to "Injected successfully"
        )
    }

    @POST
    @Path("/echo")
    fun echo(request: TestRequest): TestResponse {
        logger.info("Echo test called with: ${request.message}")
        return TestResponse(
            originalMessage = request.message,
            timestamp = System.currentTimeMillis(),
            status = "success"
        )
    }
}

data class TestRequest(
    val message: String? = null
)

data class TestResponse(
    val originalMessage: String? = null,
    val timestamp: Long = 0,
    val status: String = "unknown"
)
