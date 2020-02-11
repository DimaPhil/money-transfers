package command

import request.BankAccountInfoRequest
import response.BankAccountInfoResponse
import service.live.BankAccountServiceLive
import validator.BankAccountInfoRequestValidator

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class BankAccountInfoCommand(
    private val bankAccountInfoRequestValidator: BankAccountInfoRequestValidator,
    private val bankAccountService: BankAccountServiceLive
) : ValueCommand<BankAccountInfoRequest, BankAccountInfoResponse> {
    override suspend fun process(bankAccountInfoRequest: BankAccountInfoRequest): BankAccountInfoResponse {
        bankAccountInfoRequestValidator.validate(bankAccountInfoRequest)
        val account = bankAccountService.bankAccountInfo(bankAccountInfoRequest.accountId)
        return BankAccountInfoResponse(
            bankAccountInfoRequest.accountId,
            account.firstName,
            account.lastName,
            account.amount
        )
    }
}
