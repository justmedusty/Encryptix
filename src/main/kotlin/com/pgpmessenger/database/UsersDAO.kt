package com.pgpmessenger.database

import com.pgpmessenger.functionality.login.hashPassword
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

object Users : Table(name = "public.'Users'") {
    val id: Column<Int> = integer("id").autoIncrement()
    val userName: Column<String> = varchar("user_name", 45).uniqueIndex()
    val publicKey: Column<String> = text("public_key").uniqueIndex()
    val passwordHash = text("password_hash")

    override val primaryKey = PrimaryKey(id)
}

/**
 * User
 *
 * @property userName
 * @property publicKey
 * @property passwordHash
 * @constructor Create empty User
 */
data class User(
    val userName: String,
    val publicKey: String,
    val passwordHash: String
)

/**
 * Configure database
 *
 */



/**
 * User name already exists
 *
 * @param userName
 * @return
 */
fun userNameAlreadyExists(userName: String): Boolean {
    return transaction {
        Users.select { Users.userName eq userName }
            .count() > 0
    }
}

/**
 * Public key already exists
 *
 * @param publicKey
 * @return
 */
fun publicKeyAlreadyExists(publicKey: String): Boolean {
    return transaction {
        Users.select { Users.publicKey eq publicKey }
            .count() > 0
    }
}

/**
 * Verify credentials
 *
 * @param userName
 * @param password
 * @return
 *///this was a major pain in the cock to get the hashing to work
fun verifyCredentials(userName: String, password: String): Boolean {
    return transaction {
        val user = Users.select { Users.userName eq userName }.singleOrNull()
        user != null && BCrypt.checkpw(password, user[Users.passwordHash])

    }
}

/**
 * User and password validation
 *
 * @param userName
 * @param password
 * @return
 */
fun userAndPasswordValidation(userName: String, password: String): Boolean {
    return when {
        password.isEmpty() && !userName.isEmpty() -> {
            if (userNameAlreadyExists(userName)) {
                false
            } else userName.length in 6..45
        }

        !password.isEmpty() && userName.isEmpty() -> {
            password.length >= 8
        }

        else -> {
            throw IllegalArgumentException("Unknown error")
        }
    }
}

/**
 * Create user
 *
 * @param user
 */// Functions to perform CRUD operations on Users table
fun createUser(user: User) {
    return transaction {
        if (userAndPasswordValidation(user.userName, "") && userAndPasswordValidation("", user.passwordHash)) {
            Users.insert {
                it[userName] = user.userName
                it[passwordHash] = hashPassword(user.passwordHash)
            } get Users.id

        }


    }


}

/**
 * Get user id
 *
 * @param userName
 * @return
 */
fun getUserId(userName: String): Int {
    return transaction {
        Users.select { Users.userName eq userName }.singleOrNull()?.get(Users.id)!!
    }
}

/**
 * Get user name
 *
 * @param id
 * @return
 */
fun getUserName(id: String?): String? {
    val userId = id?.toIntOrNull() // Convert the String ID to Int or adjust the conversion based on the actual ID type

    return transaction {
        userId?.let { convertedId ->
            Users.select { Users.id eq convertedId }.singleOrNull()?.get(Users.userName)
        }
    }
}

fun getPublicKey(userName: String): String? {
    return transaction {
        val result = Users.select { Users.userName eq userName }.singleOrNull()
        result?.get(Users.publicKey)

    }
}


/**
 * Update public key
 *
 * @param userName
 * @param newPublicKey
 * @return
 */
fun updatePublicKey(userName: String, newPublicKey: String): Boolean {
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

/**
 * Update user credentials
 *
 * @param userName
 * @param password
 * @param newValue
 */
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

/**
 * Delete user
 *
 * @param id
 */
fun deleteUser(id: Int) {
    transaction {
        Users.deleteWhere { Users.id eq id }
    }
}


