package request

import java.math.BigDecimal

/**
 * The request for withdrawing some money from the user account.
 *
 * @param from The account id from which the money should be withdrawn.
 * @param amount The amount of money need to be withdrawn.
 */
data class WithdrawRequest(
    val from: Long,
    val amount: BigDecimal
)
