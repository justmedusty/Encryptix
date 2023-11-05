package com.pgpmessenger.resources

import com.pgpmessenger.security.CreateJWT
import com.pgpmessenger.security.JWTConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    routing {

        get("/jwt/req") {
            call.respondText(CreateJWT(JWTConfig("dustyns web app","https://jwt-provider-domain/","secret","dustyn",700000)))

        }
        authenticate("jwt"){
            get("/protected"){
                call.respondText("You got protected info")
            }
        }

    }
}
