package extensions

import exception.UserDataValidationException
import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import kotlin.reflect.KClass

suspend inline fun <reified T: Any> ApplicationCall.receiveOrBadRequest(
    errorMessage: String = "Unable to parse json body"
): T = receiveJsonOrNull(T::class, errorMessage)

suspend inline fun <T: Any> ApplicationCall.receiveJsonOrNull(type: KClass<T>, errorMessage: String): T {
    return try {
        receive(type)
    } catch (cause: Throwable) {
        throw UserDataValidationException(errorMessage)
    }
}
