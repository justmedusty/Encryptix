package com.pgpmessenger.database

import com.pgpmessenger.functionality.login.hashPassword
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

object Users : Table(name = "public.'Users'") {
    val id: Column<Int> = integer("id").autoIncrement()
    val userName: Column<String> = varchar("user_name",45)
    val publicKey: Column<String> = text("public_key")
    val passwordHash = text("password_hash")

    override val primaryKey = PrimaryKey(id)
}

data class User(
    val userName : String,
    val publicKey: String,
    val passwordHash : String
)

fun Application.configureDatabase() {
    val url = System.getenv("POSTGRES_URL")
    val user = System.getenv("POSTGRES_USER")
    val password = System.getenv("POSTGRES_PASSWORD")

    try {
        Database.connect(url, driver="org.postgresql.Driver", user = user,password = password )

    }
    catch (e : Exception){
        println(e)
    }

    transaction {
        SchemaUtils.create(Users)
        //dont really need this but fuck it for now
        addLogger(StdOutSqlLogger)
    }
}

fun userAndPasswordValidation(userName: String, password: String): Boolean {
    return when {
        password.isNullOrEmpty() && !userName.isNullOrEmpty() -> {
            if (userNameAlreadyExists(userName)) {
                false
            } else userName.length in 6..45
        }
        !password.isNullOrEmpty() && userName.isEmpty() -> {
            password.length >= 8
        }
        else -> {
            throw IllegalArgumentException("Unknown error")
        }
    }
}
// Functions to perform CRUD operations on Users table
fun createUser(user: User) {
    return transaction {
       if (userAndPasswordValidation(user.userName,"") && userAndPasswordValidation("",user.passwordHash)){
           Users.insert {
               it[userName] = user.userName
               it[passwordHash] = hashPassword(user.passwordHash)
           } get Users.id

       }


        }


}

fun getUserId(userName: String): Int {
    return transaction {
        Users.select { Users.userName eq userName }.singleOrNull()?.get(Users.id)!!
    }
}

fun getUserName(id: String?): String? {
    val userId = id?.toIntOrNull() // Convert the String ID to Int or adjust the conversion based on the actual ID type

    return transaction {
        userId?.let { convertedId ->
            Users.select { Users.id eq convertedId }.singleOrNull()?.get(Users.userName)
        }
    }
}

fun updatePublicKey(userName: String,newPublicKey : String): Boolean {
    if (publicKeyAlreadyExists(newPublicKey)) {
        return false
    } else {
        transaction {

            Users.update({ Users.userName eq userName }) {
                it[publicKey] = newPublicKey
            }
        }
        return true
    }
}

fun updateUserCredentials(userName: String, password: String, newValue: String) {
    transaction {
        when {
            password.isEmpty() && newValue.isNotEmpty() -> {
                Users.update({ Users.userName eq userName }) {
                    it[Users.userName] = newValue
                }
            }
            password.isNotEmpty() && newValue.isNotEmpty() -> {
                Users.update({ Users.userName eq userName }) {
                    it[passwordHash] = hashPassword(newValue)
                }
            }
            else -> {
                throw IllegalArgumentException("An error occurred during the update")
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
        user != null && BCrypt.checkpw(password,user[Users.passwordHash])

    }
}


