package controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import controller.TestRequestsHelper.Companion.createAccount
import controller.TestRequestsHelper.Companion.deposit
import controller.TestRequestsHelper.Companion.getAccount
import controller.TestRequestsHelper.Companion.testIsBadRequest
import controller.TestRequestsHelper.Companion.testIsOk
import controller.TestRequestsHelper.Companion.transfer
import controller.TestRequestsHelper.Companion.withdraw
import io.ktor.application.Application
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import response.TotalAmountResponse
import response.BankAccountInfoResponse
import serverConfiguration
import java.math.BigDecimal

class BankOperationsControllerTest {
    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `deposit money to account successfully`() = withTestApplication(Application::serverConfiguration) {
        createAccount(firstName = "Dmitry", lastName = "Filippov")

        testIsOk(deposit(accountId = 1, amount = BigDecimal("2.39"))) {
            assertEquals(
                TotalAmountResponse(accountId = 1, totalAmount = BigDecimal("2.39")),
                objectMapper.readValue<TotalAmountResponse>(response.content!!)
            )
        }
        testIsOk(deposit(accountId = 1, amount = BigDecimal("4.0000000000000000001"))) {
            assertEquals(
                TotalAmountResponse(
                    accountId = 1,
                    totalAmount = BigDecimal("6.3900000000000000001")
                ),
                objectMapper.readValue<TotalAmountResponse>(response.content!!)
            )
        }
    }

    @Test
    fun `fail on incorrect deposit requests`() = withTestApplication(Application::serverConfiguration) {
        createAccount(firstName = "Dmitry", lastName = "Filippov")

        testIsBadRequest(deposit(accountId = 1, amount = BigDecimal.ZERO))
        testIsBadRequest(deposit(accountId = 1, amount = BigDecimal("-1.29")))
        testIsBadRequest(deposit(accountId = 0, amount = BigDecimal("2.39")))
        testIsBadRequest(deposit(accountId = 2, amount = BigDecimal("2.39")))
    }

    @Test
    fun `withdraw money from the account successfully`() = withTestApplication(Application::serverConfiguration) {
        createAccount(firstName = "Dmitry", lastName = "Filippov")
        deposit(accountId = 1, amount = BigDecimal("2.39"))

        testIsOk(withdraw(accountId = 1, amount = BigDecimal("1.38"))) {
            assertEquals(
                TotalAmountResponse(accountId = 1, totalAmount = BigDecimal("1.01")),
                objectMapper.readValue<TotalAmountResponse>(response.content!!)
            )
        }
        testIsBadRequest(withdraw(accountId = 1, amount = BigDecimal("1.02"))) // not enough money for withdrawal
        testIsOk(withdraw(accountId = 1, amount = BigDecimal("1.01"))) {
            assertEquals(
                TotalAmountResponse(accountId = 1, totalAmount = BigDecimal("0.00")),
                objectMapper.readValue<TotalAmountResponse>(response.content!!)
            )
        }
    }

    @Test
    fun `fail on incorrect withdrawal requests`() = withTestApplication(Application::serverConfiguration) {
        createAccount(firstName = "Dmitry", lastName = "Filippov")
        deposit(accountId = 1, amount = BigDecimal("2.39"))

        testIsBadRequest(withdraw(accountId = 0, amount = BigDecimal("1.02")))
        testIsBadRequest(withdraw(accountId = 1, amount = BigDecimal.ZERO))
        testIsBadRequest(withdraw(accountId = 1, amount = BigDecimal("-1.02")))
    }

    @Test
    fun `successfully trasfer money between accounts`() = withTestApplication(Application::serverConfiguration) {
        createAccount(firstName = "Dmitry", lastName = "Filippov")
        createAccount(firstName = "Ivan", lastName = "Ivanov")
        deposit(accountId = 1, amount = BigDecimal("2.39"))

        testIsOk(transfer(fromId = 1, toId = 2, amount = BigDecimal("1.38")))
        testIsOk(getAccount(accountId = 1)) {
            assertEquals(
                BankAccountInfoResponse(accountId = 1, firstName = "Dmitry", lastName = "Filippov", amount = BigDecimal("1.01")),
                objectMapper.readValue<BankAccountInfoResponse>(response.content!!)
            )
        }
        testIsOk(getAccount(accountId = 2)) {
            assertEquals(
                BankAccountInfoResponse(accountId = 2, firstName = "Ivan", lastName = "Ivanov", amount = BigDecimal("1.38")),
                objectMapper.readValue<BankAccountInfoResponse>(response.content!!)
            )
        }
        testIsBadRequest(transfer(fromId = 1, toId = 2, amount = BigDecimal("1.02"))) // not enough money for transfer
        testIsOk(transfer(fromId = 1, toId = 2, amount = BigDecimal("1.01")))
    }

    @Test
    fun `fail on incorrect transfer requests`() = withTestApplication(Application::serverConfiguration) {
        createAccount(firstName = "Dmitry", lastName = "Filippov")
        createAccount(firstName = "Ivan", lastName = "Ivanov")
        deposit(accountId = 1, amount = BigDecimal("2.39"))

        testIsBadRequest(transfer(fromId = 0, toId = 2, amount = BigDecimal("1.02")))
        testIsBadRequest(transfer(fromId = 1, toId = 0, amount = BigDecimal("1.02")))
        testIsBadRequest(transfer(fromId = 1, toId = 2, amount = BigDecimal.ZERO))
        testIsBadRequest(transfer(fromId = 1, toId = 2, amount = BigDecimal("-1.02")))
    }
}
