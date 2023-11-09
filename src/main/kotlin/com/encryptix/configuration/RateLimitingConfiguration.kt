package com.encryptix.configuration

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.minutes

data class RateLimitConfig(var lastRequestTime: Long, var requestCount: Int)

val rateLimitMap = ConcurrentHashMap<String, RateLimitConfig>()

fun Application.configureRateLimiting() {
    intercept(Plugins) {
        val ip = call.request.origin.remoteHost
        val currentTime = System.currentTimeMillis()
        val rateLimitInfo = rateLimitMap.getOrPut(ip) { RateLimitConfig(currentTime, 0) }

        if (call.request.uri == "/app/login") {
            if (currentTime - rateLimitInfo.lastRequestTime > 1.minutes.inWholeMilliseconds) {
                rateLimitInfo.requestCount = 1
                rateLimitInfo.lastRequestTime = currentTime
            } else {
                rateLimitInfo.requestCount++
                if (rateLimitInfo.requestCount > 6) {
                    call.respond(
                        HttpStatusCode.TooManyRequests,
                        mapOf("Response" to "Too many requests, rate limit exceeded"),
                    )
                    finish()
                }
            }
        } else {
            if (currentTime - rateLimitInfo.lastRequestTime > 1.minutes.inWholeMilliseconds) {
                rateLimitInfo.requestCount = 1
                rateLimitInfo.lastRequestTime = currentTime
            } else {
                rateLimitInfo.requestCount++
                if (rateLimitInfo.requestCount > 60) {
                    call.respond(
                        HttpStatusCode.TooManyRequests,
                        mapOf("Response" to "Too many requests, rate limit exceeded"),
                    )
                    finish()
                }
            }
        }
    }
}
