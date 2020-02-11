package service.deferred.operation

import java.math.BigDecimal

class DepositOperation(
    val toId: Long,
    val amount: BigDecimal
) : Operation()
