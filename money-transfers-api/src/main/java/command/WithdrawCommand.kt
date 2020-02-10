package command

import request.WithdrawRequest
import response.TotalAmountResponse
import service.BankAccountService
import validator.WithdrawRequestValidator

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class WithdrawCommand(
    private val withdrawRequestValidator: WithdrawRequestValidator,
    private val bankAccountService: BankAccountService
) : ValueCommand<WithdrawRequest, TotalAmountResponse> {
    override suspend fun process(withdrawRequest: WithdrawRequest): TotalAmountResponse {
        withdrawRequestValidator.validate(withdrawRequest)
        return TotalAmountResponse(
            withdrawRequest.from,
            bankAccountService.withdraw(withdrawRequest.from, withdrawRequest.amount)
        )
    }
}
