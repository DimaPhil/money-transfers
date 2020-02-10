package service

import exception.NotEnoughBalanceException
import repository.AtomicStorage
import service.account.Account
import service.operation.AccountManagingService
import service.operation.TransferOperation
import java.math.BigDecimal

/**
 * The service which implements bank operations logic.
 * The service is supposed to be thread-safe and lock-free (unless the storage is either not thread-safe or not lock-free).
 *
 * The implementation is based on the two works below:
 * 1. "A Practical Multi-Word Compare-and-Swap Operation" by T. L. Harris et al.
 * 2. "Practical lock-freedom" by Keir Fraser.
 *
 * We use a simplified version of DCSS operation, which can be considered to be correct, because
 * [service.account.Account] instances in [accountStorage] never suffer from ABA problem.
 */
class BankAccountService(
    private val accountStorage: AtomicStorage<Long, Account>,
    private val accountManagingService: AccountManagingService
) {
    fun createBankAccount(firstName: String, lastName: String): Long {
        return accountStorage.add(Account(firstName, lastName, BigDecimal.ZERO))
    }

    fun deposit(toId: Long, amount: BigDecimal): BigDecimal {
        while (true) {
            val account = accountStorage.get(toId)
            if (!account.invokeOperation()) {
                val updatedAccount = account.copy(account.amount + amount)
                if (accountStorage.compareAndSet(toId, account, updatedAccount)) {
                    return updatedAccount.amount
                }
            }
        }
    }

    fun withdraw(fromId: Long, amount: BigDecimal): BigDecimal {
        while (true) {
            val account = accountStorage.get(fromId)

            if (!account.invokeOperation()) {
                if (account.amount < amount) {
                    throw NotEnoughBalanceException("The current balance is not enough to perform withdraw operation")
                }
                val updatedAccount = account.copy(account.amount - amount)
                if (accountStorage.compareAndSet(fromId, account, updatedAccount)) {
                    return updatedAccount.amount
                }
            }
        }
    }

    fun bankAccountInfo(accountId: Long): Account {
        while (true) {
            val account = accountStorage.get(accountId)
            if (!account.invokeOperation()) {
                return account
            }
        }
    }

    fun transfer(fromId: Long, toId: Long, amount: BigDecimal) {
        TransferOperation(accountManagingService, fromId, toId, amount).invoke()
    }
}
