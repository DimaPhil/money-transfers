package controller

import command.DepositCommand
import command.TransferCommand
import command.WithdrawCommand
import extensions.receiveOrBadRequest
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import io.ktor.routing.route
import org.kodein.di.Kodein
import request.DepositRequest
import request.TransferRequest
import request.WithdrawRequest

class BankOperationsController(
    kodein: Kodein,
    private val depositCommand: DepositCommand,
    private val transferCommand: TransferCommand,
    private val withdrawCommand: WithdrawCommand
) : AbstractController(kodein) {
    override fun Routing.registerRoutes() {
        route("/operations") {
            post("/deposit") {
                val depositRequest = call.receiveOrBadRequest<DepositRequest>(
                    errorMessage = """Unable to parse deposit request. Hint: {"to": <number>, "amount": <decimal number>}"""
                )
                val newAmountDto = depositCommand.process(depositRequest)
                call.respond(HttpStatusCode.OK, newAmountDto)
            }

            post("/transfer") {
                val transferRequest = call.receiveOrBadRequest<TransferRequest>(
                    errorMessage = """Unable to parse transfer request. Hint: {"from": <number>, "to": <number>, "amount": <decimal number>}"""
                )
                transferCommand.process(transferRequest)
                call.respond(HttpStatusCode.OK)
            }

            post("/withdraw") {
                val withdrawRequest = call.receiveOrBadRequest<WithdrawRequest>(
                    errorMessage = """Unable to parse withdraw request. Hint: {"from": <number>, "amount": <decimal number>}"""
                )
                val newAmountDto = withdrawCommand.process(withdrawRequest)
                call.respond(HttpStatusCode.OK, newAmountDto)
            }
        }
    }
}
