package command

import request.CreateAccountRequest
import service.BankAccountService
import validator.CreateAccountRequestValidator

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class CreateAccountCommand(
    private val createAccountRequestValidator: CreateAccountRequestValidator,
    private val bankAccountService: BankAccountService
) : ValueCommand<CreateAccountRequest, Long> {
    override suspend fun process(createAccountRequest: CreateAccountRequest): Long {
        createAccountRequestValidator.validate(createAccountRequest)
        return bankAccountService.createBankAccount(createAccountRequest.firstName, createAccountRequest.lastName)
    }
}
