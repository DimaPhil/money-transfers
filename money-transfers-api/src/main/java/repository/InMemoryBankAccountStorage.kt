package repository

import exception.UserDataValidationException
import service.live.account.Account
import java.util.concurrent.ConcurrentHashMap

/**
 * This class implements logic for an atomic storage of users accounts.
 *
 * This implementation is the easiest implementation which uses [ConcurrentHashMap] as a storage.
 * This approach is not scalable and not fully lock-free, however
 * for the sake of simplicity of this test it sounds like a valid solution.
 *
 * Another solution which might be valid is H2 Database (which is in-memory and thus satisfies requirements):
 * - H2 is scalable
 * - H2 makes it more easy to move application to production (because supports the same SQL commands)
 */
class InMemoryBankAccountStorage : AtomicStorage<Long, Account> {
    private val storage = ConcurrentHashMap<Long, Account>()

    override fun add(value: Account): Long {
        while (true) {
            val size = storage.size.toLong() + 1
            if (storage.putIfAbsent(size, value) == null) {
                return size
            }
        }
    }

    override fun get(key: Long): Account {
        return storage[key] ?: throw UserDataValidationException("No account id $key in the storage")
    }

    override fun compareAndSet(key: Long, oldValue: Account, newValue: Account): Boolean {
        return storage.replace(key, oldValue, newValue)
    }
}
