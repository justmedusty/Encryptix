package com.encryptix.database

import com.encryptix.database.Messages.encryptedMessage
import mu.KotlinLogging
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.util.Base64

val logger = KotlinLogging.logger { }

object Messages : Table(name = "Messages") {
    private val id: Column<Int> = integer("id").autoIncrement()
    val senderId: Column<Int> = integer("sender_id") references Users.id
    val receiverId: Column<Int> = integer("receiver_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val encryptedMessage: Column<ExposedBlob> = blob("encrypted_message")
    val timeSent: Column<LocalDateTime> = datetime("time_sent").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)

}

/**
 * Message
 *
 * @property senderUserName
 * @property receiverUserName
 * @property encryptedMessage
 * @property timeSent
 * @constructor Create empty Message
 */
data class Message(
    val senderUserName: String,
    val receiverUserName: String,
    val encryptedMessage: ByteArray,
    val timeSent: LocalDateTime,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (senderUserName != other.senderUserName) return false
        if (receiverUserName != other.receiverUserName) return false
        if (!encryptedMessage.contentEquals(other.encryptedMessage)) return false
        if (timeSent != other.timeSent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = senderUserName.hashCode()
        result = 31 * result + receiverUserName.hashCode()
        result = 31 * result + encryptedMessage.contentHashCode()
        result = 31 * result + timeSent.hashCode()
        return result
    }
}

/**
 * Get user messages
 *
 * @param id
 * @return list of messages in the Message object format
 */
fun getAllUserMessages(id: Int, page: Int, limit: Int): List<Message> {
    val offset = (page - 1) * limit
    return try {
        transaction {
            Messages.select(Messages.receiverId eq id)
                .limit(limit, offset.toLong())
                .orderBy(Messages.timeSent, SortOrder.DESC)
                .map {
                    val senderUsername: String = it[Messages.senderId].toString()
                    val receiverUserName: String = it[Messages.receiverId].toString()

                    // Decode Base64 to String
                    val base64EncodedMessage: ByteArray = it[Messages.encryptedMessage].bytes
                    val decodedMessage: String = Base64.getDecoder().decode(base64EncodedMessage).toString(StandardCharsets.UTF_8)

                    val timeSent: LocalDateTime = it[Messages.timeSent]
                    Message(
                        getUserName(senderUsername).toString(),
                        getUserName(receiverUserName).toString(),
                        decodedMessage.toByteArray(),
                        timeSent,
                    )
                }
        }
    } catch (e: Exception) {
        logger.error { "Error grabbing users $e" }
        emptyList()
    }
}

fun getUserMessagesByUserName(id: Int, senderUserName: String, page: Int, limit: Int): List<Message> {
    val senderId: Int = getUserId(senderUserName)
    val offset = (page - 1) * limit
    return try {
        transaction {
            Messages.select {
                (Messages.receiverId eq id) and (Messages.senderId eq senderId)
            }
                .limit(page, offset.toLong())
                .orderBy(Messages.timeSent, SortOrder.DESC)
                .map {
                    val senderUsername: String = it[Messages.senderId].toString()
                    val receiverUserName: String = it[Messages.receiverId].toString()
                    val encryptedMessage: ByteArray = it[encryptedMessage].bytes
                    val timeSent: LocalDateTime = it[Messages.timeSent]
                    Message(
                        getUserName(senderUsername).toString(),
                        getUserName(receiverUserName).toString(),
                        encryptedMessage,
                        timeSent,
                    )
                }
        }
    } catch (e: Exception) {
        logger.error { "Error grabbing users $e" }
        emptyList()
    }
}

fun sendMessage(senderId: Int, receiverId: Int, encryptedMessage: ByteArray, timeSent: LocalDateTime) {
    if (userNameAlreadyExists(getUserName(senderId.toString()).toString()) && userNameAlreadyExists(getUserName(senderId.toString()).toString())) {
        try {
            transaction {
                Messages.insert {
                    it[Messages.senderId] = senderId
                    it[Messages.receiverId] = receiverId
                    it[Messages.encryptedMessage] = ExposedBlob(encryptedMessage)
                    it[Messages.timeSent] = timeSent
                }
            }
        } catch (e: Exception) {
            logger.error { e }
        }
    } else {
        logger.error { "Invalid user credentials given" }
        throw IllegalArgumentException()
    }
}
