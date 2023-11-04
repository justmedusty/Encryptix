package com.test.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.Date

fun Application.configureSecurity() {
    // Please read the jwt property from the config file if you are using EngineMain
    val jwtAudience = "dustyns web app"
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
}

data class JWTConfig(
    val audience: String,
    val domain : String,
    val secret : String,
    val user : String,
    val expiresInMS : Long
)
fun CreateJWT(jwtConfig: JWTConfig) : String{
    return JWT.create()
        .withAudience(jwtConfig.audience)
        .withIssuer(jwtConfig.domain)
        .withExpiresAt(Date(System.currentTimeMillis() + jwtConfig.expiresInMS))
        .withSubject(jwtConfig.user)
        .sign(Algorithm.HMAC256(jwtConfig.secret))
}