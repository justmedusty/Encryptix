package com.pgpmessenger.database

import com.pgpmessenger.database.Messages.encryptedMessage
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object Messages : Table(name = "Messages") {
    private val id: Column<Int> = integer("id").autoIncrement()
    val senderId: Column<Int> = integer("sender_id") references Users.id
    val receiverId: Column<Int> = integer("receiver_id") references Users.id
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
    val timeSent: LocalDateTime
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
fun getUserMessages(id: Int): List<Message> {
    return transaction {
        Messages.select(Messages.receiverId eq id).map {
            val senderUsername: String = it[Messages.senderId].toString()
            val receiverUserName: String = it[Messages.receiverId].toString()
            val encryptedMessage: ByteArray = it[encryptedMessage].bytes
            val timeSent: LocalDateTime = it[Messages.timeSent]
            Message(
                senderUsername,
                receiverUserName,
                encryptedMessage,
                timeSent
            )

        }

    }


}

fun sendMessage(senderId: Int, receiverId: Int, encryptedMessage: ByteArray, timeSent: LocalDateTime) {

    transaction {
        Messages.insert {
            it[Messages.senderId] = senderId
            it[Messages.receiverId] = receiverId
            it[Messages.encryptedMessage] = ExposedBlob(encryptedMessage)
            it[Messages.timeSent] = timeSent
        }
    }
}





