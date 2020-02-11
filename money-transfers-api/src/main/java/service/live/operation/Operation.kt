package service.live.operation

abstract class Operation {
    @Volatile
    var isCompleted: Boolean = false

    abstract fun invoke()
}
