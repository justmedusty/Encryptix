package com.test

import com.test.database.configureDatabase
import com.test.security.configureSecurity
import com.test.resources.configureHTTP
import com.test.resources.configureRouting
import com.test.functionality.configureSerialization
import com.test.functionality.login.configureLogin
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.h2.engine.Database

fun main() {
    embeddedServer(Netty, port = 6969, host = "0.0.0.0", module = Application::module)
            .start(wait = true)


}

fun Application.module() {
    configureSecurity()
    configureHTTP()
    configureSerialization()
    //configureDatabase()
    configureRouting()
    configureLogin()
}
