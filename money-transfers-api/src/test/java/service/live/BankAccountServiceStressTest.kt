package service.live

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import repository.InMemoryBankAccountStorage
import service.live.account.Account
import service.live.operation.AccountManagingService
import java.math.BigDecimal
import java.util.concurrent.Phaser
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicReferenceArray

class BankAccountServiceStressTest {
    private val accountStorage = InMemoryBankAccountStorage()
    private val accountManagingService = AccountManagingService(accountStorage)
    private val bankAccountService = BankAccountServiceLive(accountStorage, accountManagingService)

    private val phaser = Phaser(THREADS + 1)
    private val expectedCurrent = AtomicReferenceArray<BigDecimal>(N + 1)
    @Volatile
    private var isFailed = false
    private var dummy: Account? = null

    @Test
    fun `pass multithreaded stress-test`() {
        initializeBankAccounts()
        repeat { accountId ->
            bankAccountService.deposit(accountId, START)
        }
        repeat { accountId ->
            assertEquals(START, bankAccountService.bankAccountInfo(accountId).amount)
            expectedCurrent.set(accountId.toInt(), START)
        }
        val testThreads = Array(THREADS) { threadId ->
            val thread = TestThread(threadId)
            thread.start()
            thread
        }
        repeat(TOTAL_PHASES) {
            println("Running phase ${it + 1}/$TOTAL_PHASES")
            if (isFailed) {
                return
            }
            if (phaser.arriveAndAwaitAdvance() < 0) {
                return
            }
            if (phaser.arriveAndAwaitAdvance() < 0) {
                return
            }
        }
        testThreads.forEach { it.join() }
        assertFalse(isFailed)
    }

    private fun repeat(action: (Long) -> Unit) {
        repeat(N) { action(it.toLong() + 1) }
    }

    private fun initializeBankAccounts() {
        repeat(N) {
            bankAccountService.createBankAccount(firstName = it.toString(), lastName = it.toString())
        }
    }

    private inner class TestThread(threadId: Int) : Thread("TestThread-$threadId") {
        private lateinit var rnd: ThreadLocalRandom

        override fun run() {
            rnd = ThreadLocalRandom.current()
            try {
                var phaseId = 1
                while (phaseId <= TOTAL_PHASES && !isFailed) {
                    runPhase()
                    phaseId++
                }
            } catch (any: Throwable) {
                any.printStackTrace()
                isFailed = true
                phaser.forceTermination()
            }
        }

        private fun runPhase() {
            verifyState()
            if (phaser.arriveAndAwaitAdvance() < 0) {
                return
            }
            var operations = 0L
            val tillTimeMillis = System.currentTimeMillis() + PHASE_DURATION_MILLIS
            do {
                runOperation()
                operations++
            } while (System.currentTimeMillis() < tillTimeMillis)
            phaser.arriveAndAwaitAdvance()
        }

        private fun verifyState() {
            repeat { accountId ->
                val expectedCurrentAmount = expectedCurrent.get(accountId.toInt())
                assertEquals(expectedCurrentAmount, bankAccountService.bankAccountInfo(accountId).amount)
            }
        }

        private fun runOperation() {
            val operationId = rnd.nextInt(100)
            val accountId = rnd.nextInt(N) + 1
            when(operationId % 4) {
                0 -> {
                    val amount = nextRoundAmount()
                    bankAccountService.deposit(accountId.toLong(), amount)
                    expectedCurrent.updateAndGet(accountId) { it + amount }
                }
                1 -> {
                    val amount = nextRoundAmount()
                    bankAccountService.withdraw(accountId.toLong(), amount)
                    expectedCurrent.updateAndGet(accountId) { it - amount }
                }
                2 -> {
                    var accountIdTo = rnd.nextInt(N - 1) + 1
                    if (accountIdTo >= accountId) {
                        accountIdTo++
                    }
                    // arbitrary amount is transferred between accounts
                    val amount = nextAmount()
                    bankAccountService.transfer(accountId.toLong(), accountIdTo.toLong(), amount)
                    expectedCurrent.updateAndGet(accountId) { it - amount }
                    expectedCurrent.updateAndGet(accountIdTo) { it + amount }
                }
                3 -> {
                    // NOTE: the result does not have to be equal to expected.
                    dummy = bankAccountService.bankAccountInfo(accountId.toLong())
                }
            }
        }

        private fun nextRoundAmount() = (nextAmount() + MOD - BigDecimal.ONE) / MOD * MOD

        private fun nextAmount() = BigDecimal((rnd.nextInt(NEXT_AMOUNT) + 1).toString())
    }

    companion object {
        private const val N = 100
        private val START = BigDecimal("1000000000.0")
        private const val NEXT_AMOUNT = 1000 // NEXT_AMOUNT << START, so that probability of over/under flow is negligible
        private val MOD = BigDecimal("100.0")
        private const val THREADS = 8
        private const val TOTAL_PHASES = 20
        private const val PHASE_DURATION_MILLIS = 1000
    }
}
