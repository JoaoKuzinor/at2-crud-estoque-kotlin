package com.estocadao

import com.estocadao.config.configureRouting
import com.estocadao.config.configureSerialization
import com.estocadao.config.configureStatusPages
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(
        Netty,
        port = System.getenv("PORT")?.toInt() ?: 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureStatusPages()
    configureRouting()
}
