package service.live.operation

import exception.NotEnoughBalanceException
import service.live.account.AcquiredAccount
import java.math.BigDecimal
import kotlin.math.max
import kotlin.math.min

class TransferOperation(
    private val accountManagingService: AccountManagingService,
    private val fromId: Long,
    private val toId: Long,
    private val amount: BigDecimal
) : Operation() {

    override fun invoke() {
        val (from, to) = acquireTransferAccounts() ?: return
        try {
            if (amount > from.amount) {
                throw NotEnoughBalanceException("Transfer sender balance is not enough to perform the operation")
            }
            from.newAmount = from.amount - amount
            to.newAmount = to.amount + amount
        } finally {
            isCompleted = true
            releaseTransferAccounts()
        }
    }

    private fun acquireTransferAccounts(): Pair<AcquiredAccount, AcquiredAccount>? {
        val minId = min(fromId, toId)
        val maxId = max(fromId, toId)
        val from = accountManagingService.acquire(minId, this) ?: return null
        val to = accountManagingService.acquire(maxId, this) ?: run {
            accountManagingService.release(minId, this)
            return null
        }
        return if (fromId < toId) Pair(from, to) else Pair(to, from)
    }

    private fun releaseTransferAccounts() {
        if (fromId < toId) {
            accountManagingService.release(toId, this)
            accountManagingService.release(fromId, this)
        } else {
            accountManagingService.release(fromId, this)
            accountManagingService.release(toId, this)
        }
    }

    override fun toString() = "TransferOperation(from = $fromId, to = $toId, amount = $amount)"
}
