package com.pgpmessenger.database

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Table.PrimaryKey
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import java.sql.Blob
import java.sql.Date
import java.time.LocalDate
import java.time.LocalDateTime

object Messages : Table(name = "public.Messages") {
    private val id: Column<Int> = integer("id").autoIncrement()
    val senderId: Column<Int> = integer("sender_id") references Users.id
    val receiverId: Column<Int> = integer("receiver_id") references Users.id
    val encryptedMessage: Column<ExposedBlob> = blob("encrypted_message")
    val timeSent : Column<LocalDateTime> = datetime("time_sent").defaultExpression(CurrentDateTime)


    override val primaryKey = PrimaryKey(id)
}