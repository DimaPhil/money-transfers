package service.deferred

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import repository.AtomicStorage
import service.BankAccountService
import service.deferred.operation.DepositOperation
import service.deferred.operation.TransferOperation
import service.deferred.operation.WithdrawOperation
import service.deferred.operation.invoke.OperationInvokeService
import service.live.account.Account
import java.math.BigDecimal

class BankAccountServiceDeferred(private val accountStorage: AtomicStorage<Long, Account>,
                                 operationsInvokeService: OperationInvokeService) : BankAccountService {
    private val deferredOperationsService = DeferredOperationsService(operationsInvokeService)

    init {
        performOperationInBackground()
    }

    override fun createBankAccount(firstName: String, lastName: String): Long {
        return accountStorage.add(Account(firstName, lastName, BigDecimal.ZERO))
    }

    override fun deposit(toId: Long, amount: BigDecimal): BigDecimal {
        deferredOperationsService.add(DepositOperation(toId, amount))
        return BigDecimal.ZERO
    }

    override fun withdraw(fromId: Long, amount: BigDecimal): BigDecimal {
        deferredOperationsService.add(WithdrawOperation(fromId, amount))
        return BigDecimal.ZERO
    }

    override fun transfer(fromId: Long, toId: Long, amount: BigDecimal) {
        deferredOperationsService.add(TransferOperation(fromId, toId, amount))
    }

    override fun bankAccountInfo(accountId: Long) = accountStorage.get(accountId)

    private fun performOperationInBackground() = GlobalScope.launch {
        while (true) {
            deferredOperationsService.invoke()
        }
    }
}
