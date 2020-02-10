package request

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class DepositRequestTest {
    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `test deserialization from json is correct`() {
        val json = """{"to": 5, "amount": 2.39}"""
        val expectedDepositRequest = DepositRequest(5, BigDecimal("2.39"))

        assertRequestsEqual(expectedDepositRequest, objectMapper.readValue(json))
    }

    @Test
    fun `test serialization to json is correct`() {
        val depositRequest = DepositRequest(5, BigDecimal("2.39"))
        val serializedDepositRequest = objectMapper.writeValueAsString(depositRequest)
        val expectedJson = """{"to":5,"amount":2.39}"""

        assertEquals(expectedJson, serializedDepositRequest)
    }

    private fun assertRequestsEqual(first: DepositRequest, second: DepositRequest) {
        assertEquals(first.to, second.to)
        assertEquals(0, first.amount.compareTo(second.amount))
    }
}
