package com.test.functionality.login
import com.test.security.CreateJWT
import com.test.security.JWTConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.mindrot.jbcrypt.BCrypt


// Function to hash and salt the password
fun hashPassword(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt())

fun Application.module() {


    // Route for user login with Basic Auth


    routing {
        authenticate("basicAuth") {
            post("/app/login") {
                val principal = call.principal<UserIdPrincipal>() ?: error("Invalid credentials")

                val token = (CreateJWT(JWTConfig("dustyns web app","https://jwt-provider-domain/","secret","dustyn",700000)))
                call.respond(mapOf("token" to token))
            }
            post("/app/signup") {
                val principal = call.principal<UserIdPrincipal>() ?: error("Invalid credentials")

                val token = (CreateJWT(JWTConfig("dustyns web app","https://jwt-provider-domain/","secret","dustyn",700000)))
                call.respond(mapOf("token" to token))
            }

        }
    }
}