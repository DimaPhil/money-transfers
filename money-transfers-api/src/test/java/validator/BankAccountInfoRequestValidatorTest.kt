package validator

import exception.UserDataValidationException
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import request.BankAccountInfoRequest

class BankAccountInfoRequestValidatorTest {
    private val bankAccountInfoRequestValidator = BankAccountInfoRequestValidator()

    @Test
    fun `should not validate requests with incorrect account id`() {
        val requestZero = BankAccountInfoRequest(accountId = 0)
        val requestNegative = BankAccountInfoRequest(accountId = -2)
        assertThrows(UserDataValidationException::class.java) { bankAccountInfoRequestValidator.validate(requestZero) }
        assertThrows(UserDataValidationException::class.java) { bankAccountInfoRequestValidator.validate(requestNegative) }
    }

    @Test
    fun `should validate requests with correct account id`() {
        val request = BankAccountInfoRequest(accountId = 239)
        assertDoesNotThrow("Validation should pass on correct requests") { bankAccountInfoRequestValidator.validate(request) }
    }
}
