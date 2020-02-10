package response

import java.math.BigDecimal

data class TotalAmountResponse(
    val accountId: Long,
    val totalAmount: BigDecimal
)
