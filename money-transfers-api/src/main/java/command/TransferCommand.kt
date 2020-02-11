package command

import request.TransferRequest
import service.live.BankAccountServiceLive
import validator.TransferRequestValidator

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class TransferCommand(
    private val transferRequestValidator: TransferRequestValidator,
    private val bankAccountService: BankAccountServiceLive
) : Command<TransferRequest> {
    override suspend fun process(transferRequest: TransferRequest) {
        transferRequestValidator.validate(transferRequest)
        bankAccountService.transfer(transferRequest.from, transferRequest.to, transferRequest.amount)
    }
}
