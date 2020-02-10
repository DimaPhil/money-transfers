package service

import exception.NotEnoughBalanceException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import repository.InMemoryBankAccountStorage
import service.account.Account
import service.operation.AccountManagingService
import java.math.BigDecimal

class BankAccountServiceTest {
    private val accountStorage = InMemoryBankAccountStorage()
    private val accountManagingService = AccountManagingService(accountStorage)
    private val bankAccountService = BankAccountService(accountStorage, accountManagingService)

    @Test
    fun `test deposit functionality`() {
        val amount = BigDecimal("239.017")
        val accountId = accountStorage.add(Account(firstName = "Dmitry", lastName = "Filippov", amount = BigDecimal.ZERO))

        assertEquals(amount, bankAccountService.deposit(accountId, amount))
        assertEquals(amount, bankAccountService.bankAccountInfo(accountId).amount)

        val newAmount = BigDecimal("17.39")
        assertEquals(amount + newAmount, bankAccountService.deposit(accountId, newAmount))
        assertEquals(amount + newAmount, bankAccountService.bankAccountInfo(accountId).amount)
    }

    @Test
    fun `test withdraw functionality`() {
        val amount = BigDecimal("239.017")
        val withdrawAmount = BigDecimal("207.01")
        val accountId = accountStorage.add(Account(firstName = "Dmitry", lastName = "Filippov", amount = BigDecimal.ZERO))
        bankAccountService.deposit(accountId, amount)

        assertEquals(amount - withdrawAmount, bankAccountService.withdraw(accountId, withdrawAmount))
        assertEquals(amount - withdrawAmount, bankAccountService.bankAccountInfo(accountId).amount)

        val newWithdrawAmount = BigDecimal("17.39")
        assertEquals(amount - withdrawAmount - newWithdrawAmount, bankAccountService.withdraw(accountId, newWithdrawAmount))
        assertEquals(amount - withdrawAmount - newWithdrawAmount, bankAccountService.bankAccountInfo(accountId).amount)

        // should be not enough money to withdraw
        assertThrows(NotEnoughBalanceException::class.java) {
            bankAccountService.withdraw(accountId, newWithdrawAmount)
        }
        assertEquals(BigDecimal("0.000"), bankAccountService.withdraw(accountId, bankAccountService.bankAccountInfo(accountId).amount))
        assertEquals(BigDecimal("0.000"), bankAccountService.bankAccountInfo(accountId).amount)
    }

    @Test
    fun `test transfer functionality`() {
        val amountFrom = BigDecimal("239.017")
        val amountTo = BigDecimal("207.01")
        val transferAmount = BigDecimal("200.00")
        val accountIdFrom = accountStorage.add(Account(firstName = "Dmitry", lastName = "Filippov", amount = BigDecimal.ZERO))
        val accountIdTo = accountStorage.add(Account(firstName = "Ivan", lastName = "Ivanov", amount = BigDecimal.ZERO))
        bankAccountService.deposit(accountIdFrom, amountFrom)
        bankAccountService.deposit(accountIdTo, amountTo)

        bankAccountService.transfer(accountIdFrom, accountIdTo, transferAmount)
        assertEquals(amountFrom - transferAmount, bankAccountService.bankAccountInfo(accountIdFrom).amount)
        assertEquals(amountTo + transferAmount, bankAccountService.bankAccountInfo(accountIdTo).amount)

        // sender should not have enough money for transfer
        assertThrows(NotEnoughBalanceException::class.java) {
            bankAccountService.transfer(accountIdFrom, accountIdTo, transferAmount)
        }

        bankAccountService.transfer(accountIdTo, accountIdFrom, transferAmount)
        assertEquals(amountFrom, bankAccountService.bankAccountInfo(accountIdFrom).amount)
        assertEquals(amountTo, bankAccountService.bankAccountInfo(accountIdTo).amount)
    }
}
