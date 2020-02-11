package service.deferred.operation.invoke

import service.deferred.operation.DepositOperation
import service.deferred.operation.Operation
import service.deferred.operation.TransferOperation
import service.deferred.operation.WithdrawOperation

class OperationInvokeService(
    private val depositInvokeService: DepositInvokeService,
    private val withdrawInvokeService: WithdrawInvokeService,
    private val transferInvokeService: TransferInvokeService
) {
    fun invoke(operation: Operation) {
        when(operation) {
            is DepositOperation -> depositInvokeService.invoke(operation)
            is WithdrawOperation -> withdrawInvokeService.invoke(operation)
            is TransferOperation -> transferInvokeService.invoke(operation)
        }
    }
}
