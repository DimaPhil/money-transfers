package request

import java.math.BigDecimal

/**
 * Request for processing a deposit to the user account.
 * The deposit it going not from the other user account, but from the outside world (say, cash inserted to the ATM).
 *
 * @param to The account id the money need to be inserted to.
 * @param amount The amount of money need to be inserted.
 */
data class DepositRequest(
    val to: Long,
    val amount: BigDecimal
)
