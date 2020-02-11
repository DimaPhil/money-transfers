package service.deferred

import service.deferred.operation.OperationStatus
import java.util.UUID

class OperationMonitorService(private val deferredOperationsService: DeferredOperationsService) {
    fun operationStatus(uuid: UUID): OperationStatus = deferredOperationsService.status(uuid)
}
