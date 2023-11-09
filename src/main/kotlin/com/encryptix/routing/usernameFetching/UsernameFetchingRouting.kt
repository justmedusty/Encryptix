package com.encryptix.routing.usernameFetching

import com.encryptix.database.fetchAllUsers
import com.encryptix.database.searchAllUsers
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureUsernameFetching() {
    routing {
        authenticate("jwt") {
            get("/app/users/fetch") {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 25
                val users: List<String> = fetchAllUsers(page, limit)
                call.respond(
                    mapOf(
                        "page" to page,
                        "limit" to limit,
                        "users" to users,
                    ),
                )
            }
            get("/app/users/search") {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 25
                val query: String? = call.request.queryParameters["query"]
                if (query.isNullOrEmpty()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf(
                            "response" to "Query cannot be empty",
                        ),
                    )
                } else {
                    val results: List<String> = searchAllUsers(query, page, limit)
                    call.respond(
                        mapOf(
                            "page" to page,
                            "query" to query,
                            "limit" to limit,
                            "users" to results,
                        ),
                    )
                }
            }
        }
    }
}
