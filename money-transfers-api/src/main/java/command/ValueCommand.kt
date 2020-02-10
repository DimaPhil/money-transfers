package command

interface ValueCommand<in T, R> {
    suspend fun process(input: T): R
}
