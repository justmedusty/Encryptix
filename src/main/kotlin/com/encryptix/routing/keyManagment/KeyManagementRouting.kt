package com.encryptix.routing.keyManagment

import com.encryptix.database.*
import com.encryptix.functionality.validation.isValidOpenPGPPublicKey
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureKeyManagementRouting() {
    routing {
        authenticate("jwt") {
            post("/app/key/upload") {
                val postParams = call.receiveParameters()
                val key: String = postParams["publicKey"] ?: ""
                val principal = call.principal<JWTPrincipal>()
                val username = getUserName(principal?.payload?.subject)
                if (key.length > CheckConstraints.PUBLIC_KEY_LENGTH.MAX_SIZE) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("Response" to "Public key exceeds maximum size (16kb)")
                    )
                }
                if (isValidOpenPGPPublicKey(key)) {
                    val success: Boolean = updatePublicKey(username.toString(), key)
                    if (success) {
                        call.respond(HttpStatusCode.OK, mapOf("Response" to "Public Key Successfully Created"))
                    } else {
                        call.respond(HttpStatusCode.Conflict, mapOf("Response" to "Public Key Already Exists"))
                    }
                } else {
                    call.respond(HttpStatusCode.Conflict, mapOf("Response" to "Public key is not valid"))
                }
            }
            get("/app/key/getMyPublicKey") {
                val principal = call.principal<JWTPrincipal>()
                val id = principal?.payload?.subject

                val userId = id?.toIntOrNull()
                if (userId != null) {
                    val publicKey = getPublicKey(getUserName(userId.toString()).toString())
                    call.respond(HttpStatusCode.OK, mapOf("Response" to "$publicKey"))
                } else {
                    call.respond(HttpStatusCode.Conflict, mapOf("Response" to "No Id Found"))
                }
            }

            delete("/app/key/deleteMyPublicKey") {
                val principal = call.principal<JWTPrincipal>()
                val id = principal?.payload?.subject
                val userId = id?.toIntOrNull()
                if (userId != null && deletePublicKey(userId)) {
                    call.respond(HttpStatusCode.OK, mapOf("Response" to "Successfully deleted your public key"))
                    logger.info { "User with id $userId has deleted their public key" }
                } else {
                    call.respond(HttpStatusCode.Conflict, mapOf("Response" to "No Id Found/ Error Occurred"))
                }
            }
        }
    }
}
