package service.deferred.operation.invoke

import exception.NotEnoughBalanceException
import repository.AtomicStorage
import service.deferred.operation.TransferOperation
import service.live.account.Account

class TransferInvokeService(private val accountStorage: AtomicStorage<Long, Account>) {
    private val lockObj = Object()

    fun invoke(operation: TransferOperation) {
        /**
         * This can be implemented in a similar way as a live [TransferOperation], but for the sake of simplicity
         * leaving synchronized here, because the main solution that is tested is live solution.
         */
        synchronized(lockObj) {
            while (true) {
                val accountFrom = accountStorage.get(operation.fromId)
                val accountTo = accountStorage.get(operation.toId)
                if (accountFrom.amount < operation.amount) {
                    throw NotEnoughBalanceException("Not enough balance on the sender's account to perform transfer")
                }
                val updatedFrom = accountFrom.copy(newAmount = accountFrom.amount - operation.amount)
                val updatedTo = accountTo.copy(newAmount = accountTo.amount + operation.amount)
                accountStorage.compareAndSet(operation.fromId, accountFrom, updatedFrom)
                accountStorage.compareAndSet(operation.toId, accountTo, updatedTo)
            }
        }
    }
}
