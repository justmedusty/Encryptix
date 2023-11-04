package com.test.functionality.login
import com.test.database.User
import com.test.database.createUser
import com.test.database.userNameAlreadyExists
import com.test.security.CreateJWT
import com.test.security.JWTConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.mindrot.jbcrypt.BCrypt
import kotlin.math.sign


// Function to hash and salt the password
fun hashPassword(password: String): String {
    return BCrypt.hashpw(password,BCrypt.gensalt())
}
data class Signup(val userName: String,  val password: String)
fun Application.configureLogin() {


    // Route for user login with Basic Auth


    routing {
        authenticate("basic") {
            post("/app/login") {

                val token = (CreateJWT(JWTConfig("dustyns web app","https://jwt-provider-domain/","secret","dustyn",700000)))
                call.respond(mapOf("access_token" to token))
            }


        }
        post("/app/signup") {
            val signup = call.receive<Signup>()
            val user = User(signup.userName, null.toString(), signup.password)
            if (userNameAlreadyExists(signup.userName)){
                call.respond(mapOf("Response" to "This username is taken, please try another"))
            }
            else {
                createUser(user)
                call.respond(mapOf("Response" to "Successfully created your account"))
            }

        }
    }

}
