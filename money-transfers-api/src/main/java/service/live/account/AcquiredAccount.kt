package service.live.account

import service.live.operation.Operation
import java.math.BigDecimal

class AcquiredAccount(
    firstName: String,
    lastName: String,
    amount: BigDecimal,
    val operation: Operation
) : Account(firstName, lastName, amount) {

    var newAmount: BigDecimal = amount

    constructor(account: Account, operation: Operation) : this(account.firstName, account.lastName, account.amount, operation)

    override fun invokeOperation(): Boolean {
        operation.invoke()
        return true
    }

    override fun toString() = "AcquiredAccount(firstName = $firstName, lastName = $lastName, amount = $amount, operation = $operation)"
}
