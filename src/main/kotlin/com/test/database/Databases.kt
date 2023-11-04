package com.test.database

import com.test.database.Users.userName
import com.test.functionality.login.hashPassword
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.selects.select
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

object Users : Table(name = "public.'Users'") {
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
    val password = "password" //environment.config.property("postgres.password").getString()

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
}


// Functions to perform CRUD operations on Users table
fun createUser(user: User) {
    return transaction {
        if (userNameAlreadyExists(user.userName)) {
            throw IllegalArgumentException("User with the same user_name already exists")
        }
        else if (user.passwordHash == null || user.passwordHash.length < 8 ){
            throw IllegalArgumentException("Password must be at least 8 characters")
        }
        Users.insert {
            it[userName] = user.userName
            it[passwordHash] = hashPassword(user.passwordHash)
        } get Users.id
        println(hashPassword(user.passwordHash)+ "================================================================================================================================================")
    }
}

fun readUser(userName: String): Query {
    return transaction {
        Users.select { Users.userName eq userName }
    }
}

fun updatePublicKey(user:User) {
    transaction {
        if (verifyCredentials(user.userName,user.passwordHash)){
            Users.update({ Users.userName eq userName }) {
                it[publicKey] = user.publicKey
            }
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

//this was a major pain in the cock to get the hashing to work
fun verifyCredentials(userName: String,password: String): Boolean{
    return transaction {
        val user = Users.select { Users.userName eq userName }.singleOrNull()
        user != null && BCrypt.checkpw(password,user[Users.passwordHash],)

    }
}


