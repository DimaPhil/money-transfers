package response

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class TotalAmountResponseTest {
    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `test deserialization from json is correct`() {
        val json = """{"accountId": 5, "totalAmount": 2.39}"""
        val expectedTransferRequest = TotalAmountResponse(5, BigDecimal("2.39"))

        assertRequestsEqual(expectedTransferRequest, objectMapper.readValue(json))
    }

    @Test
    fun `test serialization to json is correct`() {
        val depositRequest = TotalAmountResponse(5, BigDecimal("2.39"))
        val serializedDepositRequest = objectMapper.writeValueAsString(depositRequest)
        val expectedJson = """{"accountId":5,"totalAmount":2.39}"""

        assertEquals(expectedJson, serializedDepositRequest)
    }

    private fun assertRequestsEqual(first: TotalAmountResponse, second: TotalAmountResponse) {
        assertEquals(first.accountId, second.accountId)
        assertEquals(0, first.totalAmount.compareTo(second.totalAmount))
    }
}
