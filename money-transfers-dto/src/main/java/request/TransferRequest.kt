package request

import java.math.BigDecimal

/**
 * The request for transferring money between two accounts.
 *
 * @param from The account id from which the money should be transferred.
 * @param to The account id to which the money should be transferred.
 * @param amount The amount of money need to be transferred.
 */
data class TransferRequest(
    val from: Long,
    val to: Long,
    val amount: BigDecimal
)
