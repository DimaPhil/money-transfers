package service.deferred.operation

import java.math.BigDecimal

class WithdrawOperation(
    val fromId: Long,
    val amount: BigDecimal
) : Operation()
