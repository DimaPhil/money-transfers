package repository

/**
 * An interface for an atomic storage.
 * All methods below are supposed to be executed atomically.
 */
interface AtomicStorage<K, V> {
    /**
     * Adds a new value to the storage and returns a key appointed to the value.
     *
     * @param value The specified value.
     */
    fun add(value: V): K

    /**
     * Returns the value for the specified key.
     *
     * @param key The storage key.
     * @return The value for the specified [key].
     */
    fun get(key: K): V

    /**
     * Atomically compares if the current oldValue for the [key] in the storage equals [oldValue],
     * and if so, changes the oldValue to the [newValue].
     *
     * @param key The storage key.
     * @param oldValue The old oldValue to compare with.
     * @param newValue The new oldValue to be set if comparison passes successfully.
     * @return `true` if comparison has passed successfully and new oldValue was set, `false` otherwise.
     */
    fun compareAndSet(key: K, oldValue: V, newValue: V): Boolean
}
