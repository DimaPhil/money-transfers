package command

import request.DepositRequest
import response.TotalAmountResponse
import service.live.BankAccountServiceLive
import validator.DepositRequestValidator

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class DepositCommand(
    private val depositRequestValidator: DepositRequestValidator,
    private val bankAccountService: BankAccountServiceLive
) : ValueCommand<DepositRequest, TotalAmountResponse> {
    override suspend fun process(depositRequest: DepositRequest): TotalAmountResponse {
        depositRequestValidator.validate(depositRequest)
        return TotalAmountResponse(
            depositRequest.to,
            bankAccountService.deposit(depositRequest.to, depositRequest.amount)
        )
    }
}
