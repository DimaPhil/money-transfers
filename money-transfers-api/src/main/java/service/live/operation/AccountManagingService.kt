package service.live.operation

import repository.AtomicStorage
import service.live.account.Account
import service.live.account.AcquiredAccount

class AccountManagingService(private val accountStorage: AtomicStorage<Long, Account>) {
    /**
     * An implementation of the restricted form of Harris DCSS operation.
     * Atomically checks that operation is not completed and acquires bank account after that -
     * after that it holds a reference to the operation.
     *
     * This method loops trying to replace the account with the provided [accountId] with
     * `new AcquiredAccount(<old-amount>, operation)` until that successfully happens.
     * As a result, returns the instance of new acquired account.
     *
     * If the current account is already "acquired" by another operation, this method helps that
     * other operation to complete by invoking [Operation.invoke] and continue trying.
     *
     * Because the accounts in the storage do not suffer from ABA problem, there is no need to implement full-blown
     * DCSS operation with descriptors for DCSS operation as explained in Harris CASN work.
     * A simple lock-free [AtomicStorage.compareAndSet] loop is enough here if [Operation.isCompleted] is checked
     * after the account is read from storage.
     *
     * @param accountId Bank account identifier.
     * @param operation Operation to apply for the account.
     * @return Acquired account which holds operation reference or null if the operation is already completed.
     */
    fun acquire(accountId: Long, operation: Operation): AcquiredAccount? {
        while (true) {
            val account = accountStorage.get(accountId)
            if (operation.isCompleted) {
                return null
            }
            if (account is AcquiredAccount) {
                if (account.operation != operation) {
                    account.invokeOperation()
                }
            } else {
                val acquiredAccount = AcquiredAccount(account, operation)
                if (operation.isCompleted) {
                    return null
                }
                if (accountStorage.compareAndSet(accountId, account, acquiredAccount)) {
                    return acquiredAccount
                }
            }
        }
    }

    /**
     * Releases an account that was previously acquired by [acquire].
     * Does nothing if the account is not currently acquired.
     *
     * NOTE: Contract for this method is that [Operation.isCompleted] should be true,
     * i.e. the operation should be completed before releasing the account.
     */
    fun release(accountId: Long, operation: Operation) {
        require(operation.isCompleted) { "Release expects operation to be completed, but it is not" }
        val account = accountStorage.get(accountId)
        if (account is AcquiredAccount) {
            if (account.operation == operation) {
                // release performs update at most once while the account is still acquired
                val updated = account.copy(account.newAmount)
                accountStorage.compareAndSet(accountId, account, updated)
            }
        }
    }
}
