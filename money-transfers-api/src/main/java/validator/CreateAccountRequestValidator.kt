package validator

import request.CreateAccountRequest

class CreateAccountRequestValidator : Validator<CreateAccountRequest>() {
    init {
        addValidation({
            request -> request.firstName.isNotBlank()
        }, "First name should not be blank")
        addValidation({
            request -> request.lastName.isNotBlank()
        }, "Last name should not be blank")
    }
}
