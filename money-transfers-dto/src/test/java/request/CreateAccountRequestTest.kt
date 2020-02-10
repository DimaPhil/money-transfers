package request

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CreateAccountRequestTest {
    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `test deserialization from json is correct`() {
        val json = """{"firstName": "Dmitry", "lastName": "Filippov"}"""
        val expectedCreateAccountRequest = CreateAccountRequest(firstName = "Dmitry", lastName = "Filippov")

        assertEquals(expectedCreateAccountRequest, objectMapper.readValue<CreateAccountRequest>(json))
    }

    @Test
    fun `test serialization to json is correct`() {
        val depositRequest = CreateAccountRequest(firstName = "Dmitry", lastName = "Filippov")
        val serializedDepositRequest = objectMapper.writeValueAsString(depositRequest)
        val expectedJson = """{"firstName":"Dmitry","lastName":"Filippov"}"""

        assertEquals(expectedJson, serializedDepositRequest)
    }
}
