package validator

import exception.UserDataValidationException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import request.WithdrawRequest
import java.math.BigDecimal

class WithdrawRequestValidatorTest {
    private val withdrawRequestValidator = WithdrawRequestValidator()

    @Test
    fun `should not validate requests with incorrect account id or amount`() {
        val requestNegativeAmount = WithdrawRequest(from = 5, amount = BigDecimal("-0.00000001"))
        val requestZeroAmount = WithdrawRequest(from = 5, amount = BigDecimal.ZERO)
        val requestZeroAccountId = WithdrawRequest(from = 0, amount = BigDecimal("2.39"))
        Assertions.assertThrows(UserDataValidationException::class.java) { withdrawRequestValidator.validate(requestNegativeAmount) }
        Assertions.assertThrows(UserDataValidationException::class.java) { withdrawRequestValidator.validate(requestZeroAmount) }
        Assertions.assertThrows(UserDataValidationException::class.java) { withdrawRequestValidator.validate(requestZeroAccountId) }
    }

    @Test
    fun `should validate requests with correct account id and amount`() {
        val request = WithdrawRequest(from = 5, amount = BigDecimal("2.39"))
        assertDoesNotThrow("Validation should pass on correct requests") { withdrawRequestValidator.validate(request) }
    }
}
