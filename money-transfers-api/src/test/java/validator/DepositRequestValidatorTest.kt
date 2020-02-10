package validator

import exception.UserDataValidationException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import request.DepositRequest
import java.math.BigDecimal

class DepositRequestValidatorTest {
    private val depositRequestValidator = DepositRequestValidator()

    @Test
    fun `should not validate requests with incorrect account id or amount`() {
        val requestNegativeAmount = DepositRequest(to = 5, amount = BigDecimal("-0.00000001"))
        val requestZeroAmount = DepositRequest(to = 5, amount = BigDecimal.ZERO)
        val requestZeroAccountId = DepositRequest(to = 0, amount = BigDecimal("2.39"))
        Assertions.assertThrows(UserDataValidationException::class.java) { depositRequestValidator.validate(requestNegativeAmount) }
        Assertions.assertThrows(UserDataValidationException::class.java) { depositRequestValidator.validate(requestZeroAmount) }
        Assertions.assertThrows(UserDataValidationException::class.java) { depositRequestValidator.validate(requestZeroAccountId) }
    }

    @Test
    fun `should validate requests with correct account id and amount`() {
        val request = DepositRequest(to = 5, amount = BigDecimal("2.39"))
        assertDoesNotThrow("Validation should pass on correct requests") { depositRequestValidator.validate(request) }
    }
}
