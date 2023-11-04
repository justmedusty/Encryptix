package com.test.database

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Users : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val userName: Column<String> = varchar("user_name",45)
    val publicKey: Column<String> = varchar("public_key",100000)
    val passwordHash = text("password_hash")

    override val primaryKey = PrimaryKey(id)
}

data class User(
    val userName : String,
    val publicKey: String,
    val passwordHash : String
)

fun Application.configureDatabase() {

    val url = "jdbc:postgresql://localhost:5432/keystore" //environment.config.property("postgres.url").getString()
    val user = "postgres"// environment.config.property("postgres.user").getString()
    val password = "fdsa" //environment.config.property("postgres.password").getString()

    try {
        Database.connect(url, driver="org.postgresql.Driver", user = user,password = password )

    }
    catch (e : Exception){
        println(e)
    }

    transaction {
        SchemaUtils.create(Users)
        addLogger(StdOutSqlLogger)
    }

    routing {
        // Create user
        post("/users") {
            val user = call.receive<User>() // Use User class instead of Users
            val id = createUser(user)
            call.respond(HttpStatusCode.Created, id)
        }

        // Read user
        get("/users/{user_name}") {
            val user_name = call.parameters["user_name"] ?: throw IllegalArgumentException("Invalid ID")
            val user = readUser(user_name).singleOrNull()
            if (user != null) {
                call.respond(HttpStatusCode.OK, user)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // Update user
        put("/users/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val user = call.receive<User>() // Use User class instead of Users
            updateUser(id, user)
            call.respond(HttpStatusCode.OK)
        }

        // Delete user
        delete("/users/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            deleteUser(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}


// Functions to perform CRUD operations on Users table
fun createUser(user: User) {
    return transaction {
        if (userNameAlreadyExists(user.userName.toString())) {
            throw IllegalArgumentException("User with the same user_name already exists")
        }
        Users.insert {
            it[userName] = user.userName
            it[publicKey] = user.publicKey
            it[passwordHash] = user.passwordHash
        } get Users.id
    }
}

fun readUser(userName: String): Query {
    return transaction {
        Users.select { Users.userName eq userName }
    }
}

fun updateUser(id: Int, user: User) {
    transaction {
        Users.update({ Users.id eq id }) {
            it[userName] = user.userName
            it[publicKey] = user.publicKey
        }
    }
}

fun deleteUser(id: Int) {
    transaction {
        Users.deleteWhere { Users.id eq id }
    }
}

fun userNameAlreadyExists(userName: String): Boolean {
    return transaction {
        Users.select { Users.userName eq userName }
            .count() > 0
    }
}

fun publicKeyAlreadyExists(publicKey: String): Boolean {
    return transaction {
        Users.select { Users.publicKey eq publicKey }
            .count() > 0
    }
}

