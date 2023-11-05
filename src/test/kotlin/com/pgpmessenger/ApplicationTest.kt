package com.pgpmessenger

import com.pgpmessenger.configuration.configureRouting
import com.pgpmessenger.security.configureSecurity
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.EmptyContent.status
import io.ktor.http.*
import io.ktor.server.testing.*
import junit.framework.TestCase.assertEquals
import org.junit.Test
import kotlin.test.*


class ApplicationTest {
    /**
    @Test
    fun testRoot() = testApplication {
        application {
            //this doesnt work because of the way the jwt secret is setup but if you take this out it wont build so just leave it goddamn it
            configureSecurity()
            configureRouting()
        }
        client.get("/").apply{
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }
    **/
}
