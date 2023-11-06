package com.pgpmessenger.functionality.profile_changes

import com.pgpmessenger.database.*
import com.pgpmessenger.functionality.isValidOpenPGPPublicKey
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Configure profile change routes
 *
 */
fun Application.configureProfileChangeRoutes() {

    routing {
        authenticate("jwt") {
            post("/app/key/upload") {
                val postParams = call.receiveParameters()
                val key = postParams["publicKey"] ?: error("No key provided")
                val principal = call.principal<JWTPrincipal>()
                val username = getUserName(principal?.payload?.subject)
                if (isValidOpenPGPPublicKey(key)) {
                    val success: Boolean = updatePublicKey(username.toString(), key)
                    if (success) {
                        call.respond(HttpStatusCode.OK, mapOf("Response" to "Public Key Successfully Created"))
                    } else {
                        call.respond(HttpStatusCode.Conflict, mapOf("Response" to "Public Key Already Exists"))
                    }
                }
            }
            post("/app/profile/changeUserName") {
                val postParams = call.receiveParameters()
                val newUserName = postParams["newUser"] ?: error("No new value provided")
                val principal = call.principal<JWTPrincipal>()
                val id = principal?.payload?.subject

                if (newUserName.isEmpty() || !userAndPasswordValidation(newUserName, "")) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("Response" to "Please provide a valid username. Must be between 6 and 45 characters and be unique")
                    )
                } else {
                    try {
                        updateUserCredentials(getUserName(id).toString(), "", newUserName)
                        call.respond(HttpStatusCode.OK, mapOf("Response" to "Username updated successfully"))
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("Response" to e.message))
                    }
                }
            }
            post("/app/profile/changePassword") {
                val postParams = call.receiveParameters()
                val newPassword = postParams["newPassword"] ?: error("No new value provided")
                val principal = call.principal<JWTPrincipal>()
                val id = principal?.payload?.subject

                if (newPassword.isEmpty() || !userAndPasswordValidation("", newPassword)) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("Response" to "Please provide a valid password. Must be at least 8 characters")
                    )
                } else {
                    try {
                        updateUserCredentials(
                            getUserName(id).toString(),
                            "this is scuffed i know but for now i dont care",
                            newPassword
                        )
                        call.respond(HttpStatusCode.OK, mapOf("Response" to "Password updated successfully"))
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("Response" to e.message))
                    }
                }
            }
            post("/app/profile/deleteAccount") {
                val principal = call.principal<JWTPrincipal>()
                val id = principal?.payload?.subject

                val userId = id?.toIntOrNull()
                if (userId != null) {
                    deleteUser(userId)
                    call.respond(HttpStatusCode.OK,mapOf("Response" to "Account Deleted"))
                } else {
                    call.respond(HttpStatusCode.Conflict,mapOf("Response" to "No Id Found"))
                }
            }
            get("/app/key/getMyPublicKey") {
                val principal = call.principal<JWTPrincipal>()
                val id = principal?.payload?.subject

                val userId = id?.toIntOrNull()
                if (userId != null) {
                    val publicKey = getPublicKey(getUserName(userId.toString()).toString())
                    call.respond(HttpStatusCode.OK,mapOf("Response" to "$publicKey"))
                } else {
                    call.respond(HttpStatusCode.Conflict,mapOf("Response" to "No Id Found"))
                }
            }

        }
    }
}

