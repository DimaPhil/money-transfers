package validator

import request.WithdrawRequest
import java.math.BigDecimal

class WithdrawRequestValidator : Validator<WithdrawRequest>() {
    init {
        addValidation({
            request -> request.from > 0
        }, "Account identifier should be greater zero")
        addValidation({
            request -> request.amount.compareTo(BigDecimal.ZERO) == 1
        }, "Withdraw amount should be greater zero")
    }
}
