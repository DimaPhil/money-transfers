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
import service.BankAccountService
import service.operation.AccountManagingService
import validator.BankAccountInfoRequestValidator
import validator.CreateAccountRequestValidator
import validator.DepositRequestValidator
import validator.TransferRequestValidator
import validator.WithdrawRequestValidator

fun Kodein.MainBuilder.setupApplication() {
    /* Repositories */
    val bankAccountRepository = InMemoryBankAccountStorage()
    bindSingleton { bankAccountRepository }

    /* Services */
    val accountManagingService = AccountManagingService(bankAccountRepository)
    val bankAccountService = BankAccountService(bankAccountRepository, accountManagingService)
    bindSingleton { accountManagingService }
    bindSingleton { bankAccountService }

    /* Validators */
    val createAccountRequestValidator = CreateAccountRequestValidator()
    val depositRequestValidator = DepositRequestValidator()
    val bankAccountInfoRequestValidator = BankAccountInfoRequestValidator()
    val transferRequestValidator = TransferRequestValidator()
    val withdrawRequestValidator = WithdrawRequestValidator()
    bindSingleton { createAccountRequestValidator }
    bindSingleton { depositRequestValidator }
    bindSingleton { bankAccountInfoRequestValidator }
    bindSingleton { transferRequestValidator }
    bindSingleton { withdrawRequestValidator }

    /* Commands */
    val createAccountCommand = CreateAccountCommand(createAccountRequestValidator, bankAccountService)
    val depositCommand = DepositCommand(depositRequestValidator, bankAccountService)
    val bankAccountInfoCommand = BankAccountInfoCommand(bankAccountInfoRequestValidator, bankAccountService)
    val transferCommand = TransferCommand(transferRequestValidator, bankAccountService)
    val withdrawCommand = WithdrawCommand(withdrawRequestValidator, bankAccountService)
    bindSingleton { createAccountCommand }
    bindSingleton { depositCommand }
    bindSingleton { bankAccountInfoCommand }
    bindSingleton { transferCommand }
    bindSingleton { withdrawCommand }

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
