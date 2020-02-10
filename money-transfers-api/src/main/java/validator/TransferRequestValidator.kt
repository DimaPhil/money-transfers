package validator

import request.TransferRequest
import java.math.BigDecimal

class TransferRequestValidator : Validator<TransferRequest>() {
    init {
        addValidation({
            request -> request.from > 0
        }, "Transfer sender account identifier should be greater zero")
        addValidation({
            request -> request.to > 0
        }, "Transfer received account identifier should be greater zero")
        addValidation({
            request -> request.from != request.to
        }, "Transfer sender and transfer received should be different accounts")
        addValidation({
            request -> request.amount.compareTo(BigDecimal.ZERO) == 1
        }, "Transfer amount should be greater zero")
    }
}
