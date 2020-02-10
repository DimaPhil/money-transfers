package response

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class BankAmountResponseTest {
    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `test deserialization from json is correct`() {
        val json = """{"accountId": 5, "firstName": "Dmitry", "lastName": "Filippov", "amount": 2.39}"""
        val expectedTransferRequest = BankAccountInfoResponse(
            accountId = 5,
            firstName = "Dmitry",
            lastName = "Filippov",
            amount = BigDecimal("2.39")
        )

        assertRequestsEqual(expectedTransferRequest, objectMapper.readValue(json))
    }

    @Test
    fun `test serialization to json is correct`() {
        val depositRequest = BankAccountInfoResponse(
            accountId = 5,
            firstName = "Dmitry",
            lastName = "Filippov",
            amount = BigDecimal("2.39")
        )
        val serializedDepositRequest = objectMapper.writeValueAsString(depositRequest)
        val expectedJson = """{"accountId":5,"firstName":"Dmitry","lastName":"Filippov","amount":2.39}"""

        assertEquals(expectedJson, serializedDepositRequest)
    }

    private fun assertRequestsEqual(first: BankAccountInfoResponse, second: BankAccountInfoResponse) {
        assertEquals(first.accountId, second.accountId)
        assertEquals(first.firstName, second.firstName)
        assertEquals(first.lastName, second.lastName)
        assertEquals(0, first.amount.compareTo(second.amount))
    }
}
