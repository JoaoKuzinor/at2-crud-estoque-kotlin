package com.estocadao.config

import com.estocadao.routes.productRoutes
import com.estocadao.routes.stockRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        productRoutes()
        stockRoutes()
    }
}
