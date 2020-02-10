package configuration

import controller.AbstractController
import io.ktor.application.Application
import io.ktor.routing.routing
import org.kodein.di.Instance
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.jvmType
import org.slf4j.LoggerFactory

fun Application.kodeinApplication(
    kodeinMapper: Kodein.MainBuilder.(Application) -> Unit = {}
) {
    val application = this

    /**
     * Creates a [Kodein] instance, binding the [Application] instance.
     * Also calls the [kodeInMapper] to map the Controller dependencies.
     */
    val kodein = Kodein {
        bind<Application>() with instance(application)
        kodeinMapper(this, application)
    }

    /**
     * Detects all the registered [AbstractController] and registers its routes.
     */
    routing {
        for (bind in kodein.container.tree.bindings) {
            val bindClass = bind.key.type.jvmType as? Class<*>?
            if (bindClass != null && AbstractController::class.java.isAssignableFrom(bindClass)) {
                val res by kodein.Instance(bind.key.type)
                logger.info("Registering '$res' routes...")
                (res as AbstractController).apply { registerRoutes() }
            }
        }
    }
}

private val logger = LoggerFactory.getLogger("KodeinApplication")
