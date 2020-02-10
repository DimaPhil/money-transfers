import configuration.kodeinApplication
import configuration.setupApplication
import exception.setupExceptionHandling
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.jackson.jackson
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.event.Level

fun Application.serverConfiguration() {
    kodeinApplication {
        install(CallLogging) {
            level = Level.INFO
        }
        install(DefaultHeaders)
        install(StatusPages) { setupExceptionHandling() }
        install(ContentNegotiation) { jackson { } }

        setupApplication()
    }
}

val server = embeddedServer(Netty, port = 8080) { serverConfiguration() }
