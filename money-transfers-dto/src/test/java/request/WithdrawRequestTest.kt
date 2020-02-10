package request

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class WithdrawRequestTest {
    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `test deserialization from json is correct`() {
        val json = """{"from": 5, "amount": 2.39}"""
        val expectedTransferRequest = WithdrawRequest(5, BigDecimal("2.39"))

        assertRequestsEqual(expectedTransferRequest, objectMapper.readValue(json))
    }

    @Test
    fun `test serialization to json is correct`() {
        val depositRequest = WithdrawRequest(5, BigDecimal("2.39"))
        val serializedDepositRequest = objectMapper.writeValueAsString(depositRequest)
        val expectedJson = """{"from":5,"amount":2.39}"""

        Assertions.assertEquals(expectedJson, serializedDepositRequest)
    }

    private fun assertRequestsEqual(first: WithdrawRequest, second: WithdrawRequest) {
        Assertions.assertEquals(first.from, second.from)
        Assertions.assertEquals(0, first.amount.compareTo(second.amount))
    }
}
