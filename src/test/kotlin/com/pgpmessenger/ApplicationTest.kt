package com.pgpmessenger

import com.pgpmessenger.database.getUserMessages
import com.pgpmessenger.functionality.encryption.saveAsGPGFile
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.Database
import org.junit.Test
import kotlin.test.assertNotNull


class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            val url = System.getenv("POSTGRES_URL")
            val user = System.getenv("POSTGRES_USER")
            val password = System.getenv("POSTGRES_PASSWORD")

            try {
                Database.connect(url, driver = "org.postgresql.Driver", user = user, password = password)

            } catch (e: Exception) {
                println(e)
            }
            val messages = getUserMessages(1)
            saveAsGPGFile(messages[0].encryptedMessage, "testing")
            assertNotNull(messages)
            println(messages)

        }
    }
}
