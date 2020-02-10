package controller

import command.BankAccountInfoCommand
import command.CreateAccountCommand
import exception.UserDataValidationException
import extensions.receiveOrBadRequest
import io.ktor.application.call
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import org.kodein.di.Kodein
import request.BankAccountInfoRequest
import request.CreateAccountRequest

class BankAccountController(
    kodein: Kodein,
    private val createAccountCommand: CreateAccountCommand,
    private val bankAccountInfoCommand: BankAccountInfoCommand
) : AbstractController(kodein) {
    override fun Routing.registerRoutes() {
        route("/accounts") {
            post {
                val createAccountRequest = call.receiveOrBadRequest<CreateAccountRequest>(
                    errorMessage = """Unable to parse create account request. Hint: {"firstName": <string>, "lastName": <string>}"""
                )
                val accountId = createAccountCommand.process(createAccountRequest)
                val location = "/accounts/$accountId"
                call.response.headers.append(HttpHeaders.Location, location)
                call.respond(HttpStatusCode.Created)
            }

            get("/{accountId}") {
                val accountId = call.parameters["accountId"]?.toLong()
                    ?: throw UserDataValidationException("Expected account id as an integer number")
                call.respond(HttpStatusCode.OK, bankAccountInfoCommand.process(BankAccountInfoRequest(accountId)))
            }
        }
    }
}
