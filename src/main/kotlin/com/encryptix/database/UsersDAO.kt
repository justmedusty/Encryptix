package com.encryptix.database

import com.encryptix.routing.login.hashPassword
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

object Users : Table(name = "Users") {
    val id: Column<Int> = integer("id").autoIncrement()
    val userName: Column<String> = varchar("user_name", 45).uniqueIndex()
    val publicKey: Column<String?> = text("public_key").uniqueIndex().nullable()
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
    val passwordHash: String,
)

/**
 * Configure database
 *
 */

/**
 * Username already exists
 *
 * @param userName
 * @return
 */
fun userNameAlreadyExists(userName: String): Boolean {
    return try {
        transaction {
            Users.select { Users.userName eq userName }.count() > 0
        }
    } catch (e: Exception) {
        logger.error { "Error checking username $e" }
        return true
    }
}

/**
 * Public key already exists
 *
 * @param publicKey
 * @return
 */
fun publicKeyAlreadyExists(publicKey: String): Boolean {
    return try {
        transaction {
            Users.select { Users.publicKey eq publicKey }.count() > 0
        }
    } catch (e: Exception) {
        logger.error { "Error checking if public key exists $e" }
        return true
    }
}

/**
 * Verify credentials
 *
 * @param userName
 * @param password
 * @return
 */ // this was a major pain in the cock to get the hashing to work
fun verifyCredentials(userName: String, password: String): Boolean {
    return try {
        transaction {
            val user = Users.select { Users.userName eq userName }.singleOrNull()
            user != null && BCrypt.checkpw(password, user[Users.passwordHash])
        }
    } catch (e: Exception) {
        logger.error { "Error verifying credentials $e" }
        return false
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
    return try {
        when {
            password.isEmpty() && userName.isNotEmpty() -> {
                if (userNameAlreadyExists(userName)) {
                    false
                } else {
                    userName.length in 6..45
                }
            }

            password.isNotEmpty() && userName.isEmpty() -> {
                password.length >= 8
            }

            else -> {
                throw IllegalArgumentException("Unknown error")
            }
        }
    } catch (e: Exception) {
        logger.error { "Error with user/pass validation $e" }
        return false
    }
}

/**
 * Create user
 *
 * @param user
 */ // Functions to perform CRUD operations on Users table
fun createUser(user: User) {
    return try {
        transaction {
            if (userAndPasswordValidation(user.userName, "") && userAndPasswordValidation("", user.passwordHash)) {
                Users.insert {
                    it[userName] = user.userName
                    it[passwordHash] = hashPassword(user.passwordHash)
                } get Users.id
            }
        }
    } catch (e: Exception) {
        logger.error { "Error creating user $e" }
    }
}

/**
 * Get user id
 *
 * @param userName
 * @return
 */
fun getUserId(userName: String): Int {
    return try {
        transaction {
            Users.select { Users.userName eq userName }.singleOrNull()?.get(Users.id)!!
        }
    } catch (e: Exception) {
        logger.error { "Error getting userID $e" }
        -1
    }
}

/**
 * Get username
 *
 * @param id
 * @return
 */
fun getUserName(id: String?): String? {
    val userId = id?.toIntOrNull() // Convert the String ID to Int or adjust the conversion based on the actual ID type

    return try {
        transaction {
            userId?.let { convertedId ->
                Users.select { Users.id eq convertedId }.singleOrNull()?.get(Users.userName)
            }
        }
    } catch (e: Exception) {
        logger.error { "Error grabbing username $e" }
        null
    }
}

/**
 * Get public key
 *
 * @param userName
 * @return
 */
fun getPublicKey(userName: String): String? {
    return try {
        transaction {
            val result = Users.select { Users.userName eq userName }.singleOrNull()
            result?.get(Users.publicKey)
        }
    } catch (e: Exception) {
        logger.error { "Error getting public key $e" }
        return null
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
    return try {
        if (publicKeyAlreadyExists(newPublicKey)) {
            false
        } else {
            transaction {
                Users.update({ Users.userName eq userName }) {
                    it[publicKey] = newPublicKey
                }
            }
            true
        }
    } catch (e: Exception) {
        logger.error { "Error updating public key $e" }
        return false
    }
}

fun deletePublicKey(id: Int): Boolean {
    return try {
        transaction {
            Users.update({ Users.id eq id }) {
                it[publicKey] = null
            }
        }
        true
    } catch (e: Exception) {
        logger.error { "Error deleting public key $e" }
        false
    }
}

/**
 * Update user credentials
 *
 * @param userName
 * @param password
 * @param newValue
 */
fun updateUserCredentials(userName: String, password: Boolean, newValue: String) {
    try {
        transaction {
            when {
                !password && newValue.isNotEmpty() -> {
                    Users.update({ Users.userName eq userName }) {
                        it[Users.userName] = newValue
                    }
                }

                password && newValue.isNotEmpty() -> {
                    Users.update({ Users.userName eq userName }) {
                        it[passwordHash] = hashPassword(newValue)
                    }
                }

                else -> {
                    throw IllegalArgumentException("An error occurred during the update")
                }
            }
        }
    } catch (e: Exception) {
        logger.error { "Error updating user credentials $e" }
    }
}

/**
 * Delete user
 *
 * @param id
 */
fun deleteUser(id: Int) {
    try {
        transaction {
            Users.deleteWhere { Users.id eq id }
        }
    } catch (e: Exception) {
        logger.error { "Error deleting user $e" }
    }
}

fun fetchAllUsers(page: Int, limit: Int): List<String> {
    val offset: Long = ((page - 1) * limit).toLong()
    return try {
        transaction {
            Users.slice(Users.userName).selectAll().limit(limit, offset).map { it[Users.userName] }
        }
    } catch (e: Exception) {
        logger.error { "Error occurred fetching users $e" }
        emptyList()
    }
}

fun searchAllUsers(query: String, page: Int, limit: Int): List<String> {
    return try {
        val offset = (page - 1) * limit
        transaction {
            Users.slice(Users.userName).select { Users.userName like "%$query%" }.limit(
                limit,
                offset.toLong(),
            ).map { it[Users.userName] }
        }
    } catch (e: Exception) {
        logger.error { "Error during search for users $e " }
        emptyList()
    }
}
