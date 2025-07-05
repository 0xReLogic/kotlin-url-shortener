package com.example.shortener.routes

import com.example.shortener.db.DatabaseFactory
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import com.example.shortener.ApplicationKt.mainModule

class UrlRoutesTest {

    @BeforeTest
    fun setup() {
        DatabaseFactory.init() // In-memory H2 for tests
    }

    @Test
    fun testShortenAndStatsFlow() = testApplication {
        application { mainModule() }

        // Shorten a URL
        val response = client.post("/api/shorten") {
            contentType(ContentType.Application.Json)
            setBody("""{"url":"https://test.com"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        val json = Json.parseToJsonElement(body).jsonObject
        val shortUrl = json["shortUrl"]?.jsonPrimitive?.content
        val shortCode = json["shortCode"]?.jsonPrimitive?.content
        assertNotNull(shortUrl)
        assertNotNull(shortCode)

        // Get stats
        val statsResp = client.get("/api/stats/$shortCode")
        assertEquals(HttpStatusCode.OK, statsResp.status)
        val statsJson = Json.parseToJsonElement(statsResp.bodyAsText()).jsonObject
        assertEquals("https://test.com", statsJson["originalUrl"]?.jsonPrimitive?.content)
        assertEquals(0, statsJson["clickCount"]?.jsonPrimitive?.int)

        // Error: non-existent code
        val notFound = client.get("/api/stats/xxxxxx")
        assertEquals(HttpStatusCode.NotFound, notFound.status)
    }

    @Test
    fun testInvalidShortenRequest() = testApplication {
        application { mainModule() }
        val response = client.post("/api/shorten") {
            contentType(ContentType.Application.Json)
            setBody("""{"url":"ftp://bad.com"}""")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}