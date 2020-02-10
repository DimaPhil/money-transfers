package validator

import exception.UserDataValidationException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import request.CreateAccountRequest

class CreateAccountRequestValidatorTest {
    private val createAccountInfoRequestValidator = CreateAccountRequestValidator()

    @Test
    fun `should not validate requests with incorrect first or last name`() {
        val requestBlankFirstName = CreateAccountRequest(firstName = "    ", lastName = "Filippov")
        val requestBlankLastName = CreateAccountRequest(firstName = "Dmitry", lastName = "     ")
        Assertions.assertThrows(UserDataValidationException::class.java) { createAccountInfoRequestValidator.validate(requestBlankFirstName) }
        Assertions.assertThrows(UserDataValidationException::class.java) { createAccountInfoRequestValidator.validate(requestBlankLastName) }
    }

    @Test
    fun `should validate requests with correct first and last name`() {
        val request = CreateAccountRequest(firstName = "Dmitry", lastName = "Filippov")
        assertDoesNotThrow("Validation should pass on correct requests") { createAccountInfoRequestValidator.validate(request) }
    }
}
