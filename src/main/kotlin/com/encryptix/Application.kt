package com.encryptix

import com.encryptix.configuration.configureRateLimiting
import com.encryptix.configuration.configureRouting
import com.encryptix.configuration.configureSecurity
import com.encryptix.configuration.configureSerialization
import com.encryptix.routing.keyManagment.configureKeyManagementRouting
import com.encryptix.routing.login.configureLogin
import com.encryptix.routing.messaging.configureMessageRoutes
import com.encryptix.routing.profileChanges.configureProfileChangeRoutes
import com.encryptix.routing.usernameFetching.configureUsernameFetching
import configureDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*

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
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Accept)
        allowHeader(HttpHeaders.AcceptLanguage)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.AccessControlAllowCredentials)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowSameOrigin = true
        allowNonSimpleContentTypes = true
        allowCredentials = true
        anyHost()


    }


}