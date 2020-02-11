package service

import service.live.account.Account
import java.math.BigDecimal

interface BankAccountService {
    fun createBankAccount(firstName: String, lastName: String): Long

    fun deposit(toId: Long, amount: BigDecimal): BigDecimal

    fun withdraw(fromId: Long, amount: BigDecimal): BigDecimal

    fun transfer(fromId: Long, toId: Long, amount: BigDecimal)

    fun bankAccountInfo(accountId: Long): Account
}
