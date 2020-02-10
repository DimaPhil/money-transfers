package controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import request.CreateAccountRequest
import response.BankAccountInfoResponse
import serverConfiguration
import java.math.BigDecimal

class BankAccountControllerTest {
    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `create correct accounts successfully`() = withTestApplication(Application::serverConfiguration) {
        with(createAccount(firstName = "Dmitry", lastName = "Filippov")) {
            assertEquals(HttpStatusCode.Created, response.status())
            assertEquals("/accounts/1", response.headers[HttpHeaders.Location])
        }
        with(createAccount(firstName = "Ivan", lastName = "Ivanov")) {
            assertEquals(HttpStatusCode.Created, response.status())
            assertEquals("/accounts/2", response.headers[HttpHeaders.Location])
        }
    }

    @Test
    fun `create incorrect account unsuccessfully`() = withTestApplication(Application::serverConfiguration) {
        with(createAccount(firstName = "    ", lastName = "Filippov")) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }
        with(createAccount(firstName = "Ivan", lastName = "     ")) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }
    }

    @Test
    fun `get existing accounts successfully and unexisting unsuccessfully`() = withTestApplication(Application::serverConfiguration) {
        createAccount(firstName = "Dmitry", lastName = "Filippov")
        createAccount(firstName = "Ivan", lastName = "Ivanov")

        with(getAccount(accountId = 0)) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }
        with(getAccount(accountId = 1)) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(
                BankAccountInfoResponse(accountId = 1, firstName = "Dmitry", lastName = "Filippov", amount = BigDecimal.ZERO),
                objectMapper.readValue<BankAccountInfoResponse>(response.content!!)
            )
        }
        with(getAccount(accountId = 2)) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(
                BankAccountInfoResponse(accountId = 2, firstName = "Ivan", lastName = "Ivanov", amount = BigDecimal.ZERO),
                objectMapper.readValue<BankAccountInfoResponse>(response.content!!)
            )
        }
        with(getAccount(accountId = 3)) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }
    }

    private fun TestApplicationEngine.getAccount(accountId: Long): TestApplicationCall {
        return handleRequest(HttpMethod.Get, "/accounts/$accountId")
    }

    private fun TestApplicationEngine.createAccount(firstName: String, lastName: String): TestApplicationCall {
        return handleRequest(HttpMethod.Post, "/accounts") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(objectMapper.writeValueAsString(CreateAccountRequest(firstName, lastName)))
        }
    }
}
