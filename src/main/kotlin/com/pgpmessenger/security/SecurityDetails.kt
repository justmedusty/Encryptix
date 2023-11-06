package com.pgpmessenger.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

data class JWTConfig(
    val audience: String,
    val domain: String,
    val secret: String,
    val id: Int,
    val expiresInMS: Long
)

fun CreateJWT(jwtConfig: JWTConfig): String {
    return JWT.create()
        .withAudience(jwtConfig.audience)
        .withIssuer(jwtConfig.domain)
        .withExpiresAt(Date(System.currentTimeMillis() + jwtConfig.expiresInMS))
        .withSubject(jwtConfig.id.toString())
        .sign(Algorithm.HMAC256(System.getenv("JWT_SECRET")))
}