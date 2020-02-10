package controller

import io.ktor.routing.Routing
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware

abstract class AbstractController(override val kodein: Kodein) : KodeinAware {
    abstract fun Routing.registerRoutes()
}
