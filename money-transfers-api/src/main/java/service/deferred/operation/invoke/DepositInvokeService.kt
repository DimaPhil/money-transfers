package service.deferred.operation.invoke

import repository.AtomicStorage
import service.deferred.operation.DepositOperation
import service.live.account.Account

class DepositInvokeService(private val accountStorage: AtomicStorage<Long, Account>) {
    fun invoke(operation: DepositOperation) {
        while (true) {
            val account = accountStorage.get(operation.toId)
            val updatedAccount = account.copy(account.amount + operation.amount)
            if (accountStorage.compareAndSet(operation.toId, account, updatedAccount)) {
                return
            }
        }
    }
}
