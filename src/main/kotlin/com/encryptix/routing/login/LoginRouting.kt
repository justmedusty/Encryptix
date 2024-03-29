package com.encryptix.routing.login

import com.encryptix.database.User
import com.encryptix.database.createUser
import com.encryptix.database.getUserId
import com.encryptix.database.userNameAlreadyExists
import com.encryptix.security.JWTConfig
import com.encryptix.security.createJWT
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.mindrot.jbcrypt.BCrypt

/**
 * Hash password
 *
 * @param password
 * @return
 */ // Function to hash and salt the password
fun hashPassword(password: String): String {
    return BCrypt.hashpw(password, BCrypt.gensalt())
}

/**
 * Signup
 *
 * @property userName
 * @property password
 * @constructor Create empty Signup
 */
data class Signup(val userName: String, val password: String)

/**
 * Configure login
 *
 */
fun Application.configureLogin() {
    // Route for user login with Basic Auth

    routing {
        authenticate("basic") {
            post("/app/login") {
                val principal = call.principal<UserIdPrincipal>() ?: error("Invalid credentials")
                val userName = principal.name
                val token = (createJWT(
                    JWTConfig(
                        "encryptix-user",
                        "https://jwt-provider-domain/",
                        System.getenv("JWT_SECRET"),
                        getUserId(userName),
                        900000,
                    ),
                ))
                call.respond(mapOf("access_token" to token))
            }

        }
        post("/app/signup") {
            val signup = call.receive<Signup>()
            val user = User(signup.userName, null.toString(), signup.password)
            when {
                user.userName.length < 6 || user.userName.length > 45 -> {
                    call.respond(
                        HttpStatusCode.Conflict,
                        mapOf("Response" to "Username must be between 6 and 45 characters")
                    )
                }

                userNameAlreadyExists(signup.userName) -> {
                    call.respond(
                        HttpStatusCode.Conflict,
                        mapOf("Response" to "This username is taken, please try another")
                    )
                }

                else -> {
                    createUser(user)
                    call.respond(HttpStatusCode.OK, mapOf("Response" to "Successfully created your account"))
                }
            }
        }
    }
}
