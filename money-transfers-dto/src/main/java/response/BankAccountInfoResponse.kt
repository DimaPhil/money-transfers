package response

import java.math.BigDecimal

data class BankAccountInfoResponse(
    val accountId: Long,
    val firstName: String,
    val lastName: String,
    val amount: BigDecimal
)
