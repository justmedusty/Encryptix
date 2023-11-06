package com.pgpmessenger

import com.pgpmessenger.configuration.configureHTTP
import com.pgpmessenger.configuration.configureRouting
import com.pgpmessenger.configuration.configureSerialization
import com.pgpmessenger.functionality.login.configureLogin
import com.pgpmessenger.functionality.profile_changes.configureProfileChangeRoutes
import com.pgpmessenger.security.configureSecurity
import configureDatabase
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
    configureDatabase()
    configureRouting()
    configureLogin()
    configureProfileChangeRoutes()
}
