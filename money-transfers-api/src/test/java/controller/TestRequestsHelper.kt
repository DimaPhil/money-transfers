package controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import org.junit.jupiter.api.Assertions.assertEquals
import request.CreateAccountRequest
import request.DepositRequest
import request.TransferRequest
import request.WithdrawRequest
import java.math.BigDecimal

class TestRequestsHelper {
    companion object {
        private val objectMapper = jacksonObjectMapper()

        fun TestApplicationEngine.deposit(accountId: Long, amount: BigDecimal): TestApplicationCall {
            return handleRequest(HttpMethod.Post, "/operations/deposit") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(objectMapper.writeValueAsString(DepositRequest(accountId, amount)))
            }
        }

        fun TestApplicationEngine.withdraw(accountId: Long, amount: BigDecimal): TestApplicationCall {
            return handleRequest(HttpMethod.Post, "/operations/withdraw") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(objectMapper.writeValueAsString(WithdrawRequest(accountId, amount)))
            }
        }

        fun TestApplicationEngine.transfer(fromId: Long, toId: Long, amount: BigDecimal): TestApplicationCall {
            return handleRequest(HttpMethod.Post, "/operations/transfer") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(objectMapper.writeValueAsString(TransferRequest(fromId, toId, amount)))
            }
        }

        fun TestApplicationEngine.getAccount(accountId: Long): TestApplicationCall {
            return handleRequest(HttpMethod.Get, "/accounts/$accountId")
        }

        fun TestApplicationEngine.createAccount(firstName: String, lastName: String): TestApplicationCall {
            return handleRequest(HttpMethod.Post, "/accounts") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(objectMapper.writeValueAsString(CreateAccountRequest(firstName, lastName)))
            }
        }

        fun testIsBadRequest(call: TestApplicationCall) {
            assertEquals(HttpStatusCode.BadRequest, call.response.status())
        }

        fun testIsOk(call: TestApplicationCall, additionalResponseChecks: TestApplicationCall.() -> Unit = {}) {
            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
                additionalResponseChecks()
            }
        }
    }
}
