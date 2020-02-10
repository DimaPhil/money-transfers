package service.operation

import exception.NotEnoughBalanceException
import service.account.AcquiredAccount
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
        if (fromId < toId) {
            val from = accountManagingService.acquire(fromId, this) ?: return null
            val to = accountManagingService.acquire(toId, this) ?: run {
                accountManagingService.release(fromId, this)
                return null
            }
            return Pair(from, to)
        } else {
            val to = accountManagingService.acquire(toId, this) ?: return null
            val from = accountManagingService.acquire(fromId, this) ?: run {
                accountManagingService.release(toId, this)
                return null
            }
            return Pair(from, to)
        }
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
