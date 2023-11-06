package com.pgpmessenger.functionality.messaging

import com.pgpmessenger.database.*
import com.pgpmessenger.functionality.encryption.encryptMessage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime

fun Application.configureMessageRoutes() {

    routing {
        authenticate("jwt") {
            post("/app/messages/send") {
                val postParams = call.receiveParameters()
                val message = postParams["message"] ?: error("No message provided")
                val receiver = postParams["receiver"] ?: error("No receiver provided")
                val principal = this.call.principal<JWTPrincipal>()
                val id = principal?.payload?.subject
                if (userNameAlreadyExists(receiver)) {
                    if (id != null) {
                        sendMessage(
                            id.toInt(),
                            getUserId(receiver),
                            encryptMessage(getPublicKey(receiver).toString(), message),
                            LocalDateTime.now()
                        )
                        call.respond(HttpStatusCode.OK, mapOf("Response" to "Message Sent"))
                    } else {
                        call.respond(HttpStatusCode.Conflict, mapOf("Response" to "Error occurred"))
                    }

                }
                else{
                    call.respond(HttpStatusCode.Conflict,mapOf("Response" to "User does not exist"))
                }
            }


            get("/app/messages/fetch") {
                val principal = call.principal<JWTPrincipal>()
                val id = principal?.payload?.subject

                val userId = id?.toIntOrNull()
                if (userId != null) {
                    val messages : List<Message> = getUserMessages(userId)
                    call.respond(HttpStatusCode.OK, mapOf("Messages" to "$messages"))
                } else {
                    call.respond(HttpStatusCode.Conflict, mapOf("Response" to "No Id Found"))
                }
            }

        }
    }
}