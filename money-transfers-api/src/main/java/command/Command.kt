package command

interface Command<in T> {
    suspend fun process(input: T)
}
