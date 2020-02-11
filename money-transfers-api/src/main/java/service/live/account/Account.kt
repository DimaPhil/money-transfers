package service.live.account

import java.math.BigDecimal

open class Account(
    val firstName: String,
    val lastName: String,
    val amount: BigDecimal
) {
    open fun invokeOperation(): Boolean = false

    fun copy(newAmount: BigDecimal) = Account(firstName, lastName, newAmount)

    override fun toString() = "Account(firstName = $firstName, lastName = $lastName, amount = $amount)"
}
