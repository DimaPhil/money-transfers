package request

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TotalAmountRequestTest {
    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `test deserialization from json is correct`() {
        val json = """{"accountId": 5}"""
        val expectedTotalAmountRequest = BankAccountInfoRequest(5)

        assertEquals(expectedTotalAmountRequest, objectMapper.readValue<BankAccountInfoRequest>(json))
    }

    @Test
    fun `test serialization to json is correct`() {
        val totalAmountRequest = BankAccountInfoRequest(5L)
        val serializedTotalAmountRequest = objectMapper.writeValueAsString(totalAmountRequest)
        val expectedJson = """{"accountId":5}"""

        assertEquals(expectedJson, serializedTotalAmountRequest)
    }
}
