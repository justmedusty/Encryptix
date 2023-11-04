package com.test.database

import org.h2.engine.Database
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.ds.PGSimpleDataSource

object UserKeys : Table("keystore") {
    val userName = varchar("user_name", 50).primaryKey()
    val keyData = bytea("key_data")
}

class UserKeysDAO {
    private val dataSource = PGSimpleDataSource().apply {
        user = "your_username"
        password = "your_password"
        serverName = "your_database_host"
        databaseName = "your_database"
    }

    init {
        Database.connect(dataSource)
        transaction {
            SchemaUtils.create(UserKeys)
        }
    }

    fun createUserKeyPair(username: String, keyData: ByteArray) {
        transaction {
            UserKeys.insert {
                it[userName] = username
                it[keyData] = keyData
            }
        }
    }

    fun readUserKeyPair(username: String): String? {
        return transaction {
            UserKeys.select { UserKeys.userName eq username }
                .map { it[UserKeys.keyData].toString(Charsets.UTF_8) }
                .firstOrNull()
        }
    }

    fun deleteUserKeyPair(username: String) {
        transaction {
            UserKeys.deleteWhere { UserKeys.userName eq username }
        }
    }
}