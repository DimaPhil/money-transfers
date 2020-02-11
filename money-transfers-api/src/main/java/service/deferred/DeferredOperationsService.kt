package service.deferred

import exception.UserDataValidationException
import service.deferred.operation.Operation
import service.deferred.operation.OperationStatus
import service.deferred.operation.invoke.OperationInvokeService
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

class DeferredOperationsService(private val operationInvokeService: OperationInvokeService) {
    private val operations = ConcurrentLinkedQueue<Operation>()
    private val operationStorage = ConcurrentHashMap<UUID, OperationStatus>()

    fun add(operation: Operation) {
        operationStorage[operation.uuid] = OperationStatus.IN_PROGRESS
        operations.add(operation)
    }

    fun status(uuid: UUID): OperationStatus {
        return operationStorage[uuid] ?: throw UserDataValidationException("Unable to get status of operation $uuid (no such operation)")
    }

    fun invoke() {
        val operation = operations.poll() ?: return
        try {
            operationInvokeService.invoke(operation)
            operationStorage[operation.uuid] = OperationStatus.SUCCESS
        } catch (any: Throwable) {
            operationStorage[operation.uuid] = OperationStatus.FAILED
        }
    }
}
