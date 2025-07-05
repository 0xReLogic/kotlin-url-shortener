package com.example.shortener

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.callloging.*
import org.slf4j.event.Level
import com.example.shortener.routes.urlShortenerRoutes
import io.ktor.server.http.content.*
import java.io.File

fun main() {
    // Initialize database and run migrations
    com.example.shortener.db.DatabaseFactory.init()

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        mainModule()
    }.start(wait = true)
}

fun Application.mainModule() {
    install(ContentNegotiation) {
        json(kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }
    install(CallLogging) {
        level = Level.INFO
    }
    routing {
        // Serve static files from resources/static directory under /assets path
        staticResources("/assets", "static")
        
        get("/") {
            call.respondRedirect("/assets/index.html")
        }
        
        get("/api") {
            call.respondText("URL Shortener API is running!")
        }
        get("/health") {
            call.respondText("OK")
        }
        urlShortenerRoutes()
    }
}