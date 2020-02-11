package service.deferred.operation

import service.deferred.operation.Operation
import java.math.BigDecimal

class TransferOperation(
    val fromId: Long,
    val toId: Long,
    val amount: BigDecimal
) : Operation()
