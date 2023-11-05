package com.test.functionality.profile_changes

import com.test.database.User
import com.test.database.updatePublicKey
import com.test.functionality.isValidOpenPGPPublicKey
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
                    call.respond(HttpStatusCode.Conflict,"Please Upload A Key")
                }
                val principal = call.principal<JWTPrincipal>()
                val username = principal?.payload?.subject.toString()
                if (isValidOpenPGPPublicKey(key)){
                    val success : Boolean = updatePublicKey(username,key)
                    if (success){
                        call.respond(HttpStatusCode.OK,"Public Key Updated")
                    }
                    else{
                        call.respond(HttpStatusCode.Conflict,"Public Key Already Exists")
                    }
                }
            }
        }
    }
}
