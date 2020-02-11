package service.deferred.operation.invoke

import exception.NotEnoughBalanceException
import repository.AtomicStorage
import service.deferred.operation.WithdrawOperation
import service.live.account.Account

class WithdrawInvokeService(private val accountStorage: AtomicStorage<Long, Account>) {
    fun invoke(operation: WithdrawOperation) {
        while (true) {
            val account = accountStorage.get(operation.fromId)
            if (account.amount < operation.amount) {
                throw NotEnoughBalanceException("There is not enough money on the bank account to process withdrawal")
            }
            val updatedAccount = account.copy(account.amount - operation.amount)
            if (accountStorage.compareAndSet(operation.fromId, account, updatedAccount)) {
                return
            }
        }
    }
}
