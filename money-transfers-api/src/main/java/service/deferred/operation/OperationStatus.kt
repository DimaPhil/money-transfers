package service.deferred.operation

enum class OperationStatus(val status: Int) {
    FAILED(0),
    IN_PROGRESS(1),
    SUCCESS(2)
}
