package com.encryptix.configuration

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.encryptix.database.verifyCredentials
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    // Please read the jwt property from the config file if you are using EngineMain
    val jwtAudience = "encryptix-user"
    val jwtDomain = "https://jwt-provider-domain/"
    val jwtRealm = "Encryptix"
    val jwtSecret = System.getenv("JWT_SECRET")
    authentication {
        jwt("jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build(),
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
                if (verifyCredentials(credentials.name, credentials.password)) UserIdPrincipal(credentials.name) else null
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
