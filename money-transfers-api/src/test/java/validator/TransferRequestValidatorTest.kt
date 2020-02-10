package validator

import exception.UserDataValidationException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import request.TransferRequest
import java.math.BigDecimal

class TransferRequestValidatorTest {
    private val transferRequestValidator = TransferRequestValidator()

    @Test
    fun `should not validate requests with incorrect from or to account ids or incorrect amount`() {
        val requestNegativeAmount = TransferRequest(from = 4, to = 5, amount = BigDecimal("-0.00000001"))
        val requestZeroAmount = TransferRequest(from = 4, to = 5, amount = BigDecimal.ZERO)
        val requestZeroFromAccountId = TransferRequest(from = 0, to = 5, amount = BigDecimal("2.39"))
        val requestZeroToAccountId = TransferRequest(from = 5, to = 0, amount = BigDecimal("2.39"))
        Assertions.assertThrows(UserDataValidationException::class.java) { transferRequestValidator.validate(requestNegativeAmount) }
        Assertions.assertThrows(UserDataValidationException::class.java) { transferRequestValidator.validate(requestZeroAmount) }
        Assertions.assertThrows(UserDataValidationException::class.java) { transferRequestValidator.validate(requestZeroFromAccountId) }
        Assertions.assertThrows(UserDataValidationException::class.java) { transferRequestValidator.validate(requestZeroToAccountId) }
    }

    @Test
    fun `should validate requests with correct from and to account id, and amount`() {
        val request = TransferRequest(from = 4, to = 5, amount = BigDecimal("2.39"))
        assertDoesNotThrow("Validation should pass on correct requests") { transferRequestValidator.validate(request) }
    }
}
