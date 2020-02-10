package validator

import request.BankAccountInfoRequest

class BankAccountInfoRequestValidator : Validator<BankAccountInfoRequest>() {
    init {
        addValidation({
            request -> request.accountId > 0
        }, "Account identifier should be greater zero")
    }
}
