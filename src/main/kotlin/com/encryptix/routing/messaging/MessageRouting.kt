package com.encryptix.routing.messaging

import com.encryptix.database.*
import com.encryptix.functionality.encryption.encryptMessage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime

/**
 * Configure message routes
 *
 */
fun Application.configureMessageRoutes() {

    routing {
        authenticate("jwt") {
            post("/app/messages/send") {
                val postParams = call.receiveParameters()
                val message = postParams["message"] ?: error("No message provided")
                val receiver = postParams["receiver"] ?: error("No receiver provided")
                val principal = this.call.principal<JWTPrincipal>()
                val id = principal?.payload?.subject
                val senderPublicKey: String = getPublicKey(getUserName(id).toString()).toString()
                val receiverPublicKey: String = getPublicKey(receiver).toString()
                if (userNameAlreadyExists(receiver) && senderPublicKey.isNotEmpty() && receiverPublicKey.isNotEmpty()) {
                    if (id != null) {
                        sendMessage(
                            id.toInt(),
                            getUserId(receiver),
                            encryptMessage(getPublicKey(receiver).toString(), message),
                            LocalDateTime.now()
                        )
                        call.respond(HttpStatusCode.OK, mapOf("Response" to "Message Sent"))
                    } else {
                        call.respond(
                            HttpStatusCode.Conflict,
                            mapOf("Response" to "Error occurred, Check that you AND the recipient have a key uploaded")
                        )
                    }

                } else {
                    call.respond(HttpStatusCode.Conflict, mapOf("Response" to "User does not exist"))
                }
            }


            get("/app/messages/fetchAll") {
                val principal = call.principal<JWTPrincipal>()
                val id = principal?.payload?.subject
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 25

                val userId = id?.toIntOrNull()
                if (userId != null) {
                    val messages: List<Message> = getAllUserMessages(userId, page, limit)
                    call.respond(
                        HttpStatusCode.OK, mapOf(
                            "Page" to page,
                            "Limit" to limit,
                            "Messages" to messages
                        )
                    )
                } else {
                    call.respond(HttpStatusCode.Conflict, mapOf("Response" to "No Id Found"))
                }
            }
            get("/app/messages/fetch") {
                val principal = call.principal<JWTPrincipal>()
                val requesterId = principal?.subject?.toInt()
                val requestedUsername = call.request.queryParameters["userName"] ?: ""
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 25

                if (requestedUsername.isNotEmpty() && requesterId != null) {
                    val messages: List<Message> = getUserMessagesByUserName(requesterId, requestedUsername, page, limit)
                    call.respond(
                        HttpStatusCode.OK,
                        mapOf(
                            "Page" to page,
                            "Limit" to limit,
                            "Messages" to messages
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("Response" to "An error occurred , please make sure the user you want to send a message to exists and is spelled properly")
                    )
                }


            }

        }
    }
}