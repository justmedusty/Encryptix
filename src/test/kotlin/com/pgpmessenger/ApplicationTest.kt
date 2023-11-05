package com.pgpmessenger

import com.pgpmessenger.configuration.configureRouting
import com.pgpmessenger.functionality.encryption.encryptMessage
import com.pgpmessenger.security.configureSecurity
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.EmptyContent.status
import io.ktor.http.*
import io.ktor.server.testing.*
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.util.*
import kotlin.test.*


class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            //this doesnt work because of the way the jwt secret is setup but if you take this out it wont build so just leave it goddamn it
           val encryptedMsg = encryptMessage("-----BEGIN PGP PUBLIC KEY BLOCK-----\n" +
                    "\n" +
                    "mDMEZUYgmRYJKwYBBAHaRw8BAQdA5zCOiIy2HhWNs4ndZZNYl27MK+uuURb+7a8A\n" +
                    "kXCYOr60JUR1c3R5biBHaWJiIDxkdXN0eW5fZ2liYkBob3RtYWlsLmNvbT6ImQQT\n" +
                    "FgoAQRYhBIPPEfdPx2Enaqv6Poh4QQcJAJDbBQJlRiCZAhsDBQkFpUP3BQsJCAcC\n" +
                    "AiICBhUKCQgLAgQWAgMBAh4HAheAAAoJEIh4QQcJAJDbPb0BAJwneb1nvldgzpHC\n" +
                    "jJNpJz/kYG7CuWVvbzCvputkFAwDAP9vgADu63jysrji8TygyOxfVIWZdPjwihO5\n" +
                    "ePjS0dzMD7g4BGVGIJkSCisGAQQBl1UBBQEBB0BEuw9nLerdRqI5eLRDslP2hn2t\n" +
                    "pVgvWdFhJ9V/AYCsZgMBCAeIfgQYFgoAJhYhBIPPEfdPx2Enaqv6Poh4QQcJAJDb\n" +
                    "BQJlRiCZAhsMBQkFpUP3AAoJEIh4QQcJAJDbgfoBAIS2cb6J2NUCxSk5i1Dhkqff\n" +
                    "uDlNzTIhee1BMs51X2B3AQDd4SGvKyn5HznZRBlrFpTPVglcMf15NbAHCFdPbOA/\n" +
                    "BQ==\n" +
                    "=LRPh\n" +
                    "-----END PGP PUBLIC KEY BLOCK-----\n","test hello dustyn")
            val outputFilePath= "src/test.gpg"
            val outputStream = BufferedOutputStream((FileOutputStream(outputFilePath)))
            outputStream.write(Base64.getDecoder().decode(encryptedMsg))
            outputStream.write(encryptedMsg.toByteArray())
            outputStream.close()

            assertNotNull(encryptedMsg)
            println(encryptedMsg)
        }
    }
}
