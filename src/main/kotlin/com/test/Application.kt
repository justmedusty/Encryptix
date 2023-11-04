package com.test

import com.test.database.configureDatabases
import com.test.security.configureSecurity
import com.test.functionality.*
import com.test.resources.configureHTTP
import com.test.resources.configureRouting
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 6969, host = "0.0.0.0", module = Application::module)
            .start(wait = true)
}

fun Application.module() {
    configureSecurity()
    configureHTTP()
    configureSerialization()
    configureDatabases()
    configureSockets()
    configureRouting()
}
