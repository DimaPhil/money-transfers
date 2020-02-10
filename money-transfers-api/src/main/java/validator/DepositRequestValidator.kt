package validator

import request.DepositRequest
import java.math.BigDecimal

class DepositRequestValidator : Validator<DepositRequest>() {
    init {
        addValidation({
            request -> request.to > 0
        }, "Account identifier should be greater zero")
        addValidation({
            request -> request.amount.compareTo(BigDecimal.ZERO) == 1
        }, "Deposit amount should be greater zero")
    }
}
