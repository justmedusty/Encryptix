package com.pgpmessenger.functionality.profile_changes

import com.pgpmessenger.database.getUserName
import com.pgpmessenger.database.updatePublicKey
import com.pgpmessenger.database.updateUserCredentials
import com.pgpmessenger.database.userAndPasswordValidation
import com.pgpmessenger.functionality.isValidOpenPGPPublicKey
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureProfileChangeRoutes(){

    routing {
        authenticate("jwt") {
            post("/app/key/upload"){
                val postParams = call.receiveParameters()
                val key = postParams["publicKey"] ?: error("No key provided")
                if(key==null){
                    call.respond(HttpStatusCode.Conflict,mapOf("Response" to "Please Upload A Key" ))
                }
                val principal = call.principal<JWTPrincipal>()
                val username = getUserName(principal?.payload?.subject)
                if (isValidOpenPGPPublicKey(key)){
                    val success : Boolean = updatePublicKey(username.toString(),key)
                    if (success){
                        call.respond(HttpStatusCode.OK,mapOf("Response" to "Public Key Successfully Created" ))
                    }
                    else{
                        call.respond(HttpStatusCode.Conflict, mapOf("Response" to "Public Key Already Exists" ))
                    }
                }
            }
            post("/app/changeUserName") {
                val postParams = call.receiveParameters()
                val newUserName = postParams["newUser"] ?: error("No new value provided")
                val principal = call.principal<JWTPrincipal>()
                val id = principal?.payload?.subject
                val token = call.request.cookies["jwt"]?: call.request.headers["Authorization"]?.removePrefix("Bearer ")

                if (newUserName.isNullOrEmpty() || !userAndPasswordValidation(newUserName,"")) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("Response" to "Please provide a valid username. Must be between 6 and 45 characters and be unique"))
                } else {
                    try {
                        updateUserCredentials(getUserName(id).toString(), "", newUserName)
                        call.respond(HttpStatusCode.OK, mapOf("Response" to "Username updated successfully"))
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("Response" to e.message))
                    }
                }
            }
        }
    }
}
