package com.encryptix

import com.encryptix.configuration.configureRateLimiting
import com.encryptix.configuration.configureRouting
import com.encryptix.configuration.configureSecurity
import com.encryptix.configuration.configureSerialization
import com.encryptix.routing.key_managment.configureKeyManagementRouting
import com.encryptix.routing.login.configureLogin
import com.encryptix.routing.messaging.configureMessageRoutes
import com.encryptix.routing.profile_changes.configureProfileChangeRoutes
import com.encryptix.routing.username_fetching.configureUsernameFetching
import configureDatabase
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 6969, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    configureSecurity()
    configureSerialization()
    configureDatabase()
    configureRouting()
    configureLogin()
    configureProfileChangeRoutes()
    configureMessageRoutes()
    configureKeyManagementRouting()
    configureRateLimiting()
    configureUsernameFetching()
}
