package request

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class TransferRequestTest {
    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `test deserialization from json is correct`() {
        val json = """{"from": 5, "to": 7, "amount": 2.39}"""
        val expectedTransferRequest = TransferRequest(5, 7, BigDecimal("2.39"))

        assertRequestsEqual(expectedTransferRequest, objectMapper.readValue(json))
    }

    @Test
    fun `test serialization to json is correct`() {
        val depositRequest = TransferRequest(5, 7, BigDecimal("2.39"))
        val serializedDepositRequest = objectMapper.writeValueAsString(depositRequest)
        val expectedJson = """{"from":5,"to":7,"amount":2.39}"""

        assertEquals(expectedJson, serializedDepositRequest)
    }

    private fun assertRequestsEqual(first: TransferRequest, second: TransferRequest) {
        assertEquals(first.from, second.from)
        assertEquals(first.to, second.to)
        assertEquals(0, first.amount.compareTo(second.amount))
    }
}
