package configuration

import command.BankAccountInfoCommand
import command.CreateAccountCommand
import command.DepositCommand
import command.TransferCommand
import command.WithdrawCommand
import controller.BankAccountController
import controller.BankOperationsController
import extensions.bindSingleton
import org.kodein.di.Kodein
import repository.InMemoryBankAccountStorage
import service.live.BankAccountServiceLive
import service.live.operation.AccountManagingService
import validator.BankAccountInfoRequestValidator
import validator.CreateAccountRequestValidator
import validator.DepositRequestValidator
import validator.TransferRequestValidator
import validator.WithdrawRequestValidator

fun Kodein.MainBuilder.setupApplication() {
    /* Repositories */
    val bankAccountRepository = InMemoryBankAccountStorage()

    /* Services */
    val accountManagingService = AccountManagingService(bankAccountRepository)
    val bankAccountService = BankAccountServiceLive(bankAccountRepository, accountManagingService)

    /* Validators */
    val createAccountRequestValidator = CreateAccountRequestValidator()
    val depositRequestValidator = DepositRequestValidator()
    val bankAccountInfoRequestValidator = BankAccountInfoRequestValidator()
    val transferRequestValidator = TransferRequestValidator()
    val withdrawRequestValidator = WithdrawRequestValidator()

    /* Commands */
    val createAccountCommand = CreateAccountCommand(createAccountRequestValidator, bankAccountService)
    val depositCommand = DepositCommand(depositRequestValidator, bankAccountService)
    val bankAccountInfoCommand = BankAccountInfoCommand(bankAccountInfoRequestValidator, bankAccountService)
    val transferCommand = TransferCommand(transferRequestValidator, bankAccountService)
    val withdrawCommand = WithdrawCommand(withdrawRequestValidator, bankAccountService)

    /* Controllers */
    bindSingleton { kodein ->
        BankAccountController(
            kodein, createAccountCommand, bankAccountInfoCommand
        )
    }
    bindSingleton { kodein ->
        BankOperationsController(
            kodein, depositCommand, transferCommand, withdrawCommand
        )
    }
}
