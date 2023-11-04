package com.test.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.test.database.verifyCredentials
import com.test.functionality.login.hashPassword
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*

fun Application.configureSecurity() {
    // Please read the jwt property from the config file if you are using EngineMain
    val jwtAudience = "jwt-audience"
    val jwtDomain = "https://jwt-provider-domain/"
    val jwtRealm = "ktor sample app"
    val jwtSecret = "secret"
    authentication {
        jwt("jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }

    authentication {
        basic(name = "basic") {
            realm = "Ktor Server"
            validate { credentials ->
                if (verifyCredentials(credentials.name, credentials.password)) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }

        form(name = "myauth2") {
            userParamName = "user"
            passwordParamName = "password"
            challenge {
                /**/
            }
        }
    }
}


data class JWTConfig(
    val audience: String,
    val domain: String,
    val secret: String,
    val user: String,
    val expiresInMS: Long
)

fun CreateJWT(jwtConfig: JWTConfig): String {
    return JWT.create()
        .withAudience(jwtConfig.audience)
        .withIssuer(jwtConfig.domain)
        .withExpiresAt(Date(System.currentTimeMillis() + jwtConfig.expiresInMS))
        .withSubject(jwtConfig.user)
        .sign(Algorithm.HMAC256(jwtConfig.secret))
}