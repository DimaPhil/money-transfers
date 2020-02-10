package exception

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext
import org.slf4j.LoggerFactory

fun StatusPages.Configuration.setupExceptionHandling() {
    exception<Throwable> { cause ->
        when(cause) {
            is UserDataValidationException -> handleBadRequest(cause)
            is NotEnoughBalanceException -> handleBadRequest(cause)
            else -> handleInternalError(cause)
        }
    }
}

private suspend fun PipelineContext<*, ApplicationCall>.handleBadRequest(exception: RuntimeException) {
    call.respond(HttpStatusCode.BadRequest, exception.message ?: "")
}

private suspend fun PipelineContext<*, ApplicationCall>.handleInternalError(exception: Throwable) {
    logger.error(exception.message, exception)
    call.respond(HttpStatusCode.InternalServerError, "Sorry, we encountered an error and are working on it.")
}

private val logger = LoggerFactory.getLogger("GlobalExceptionHandler")
