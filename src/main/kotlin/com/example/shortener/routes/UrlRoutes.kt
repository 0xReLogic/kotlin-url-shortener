package com.example.shortener.routes

import com.example.shortener.models.CreateUrlRequest
import com.example.shortener.models.UrlResponse
import com.example.shortener.services.UrlService
import com.example.shortener.services.UrlShortenResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class ShortenRequest(val url: String, val customAlias: String? = null)

@Serializable
data class ShortenResponse(val shortUrl: String, val shortCode: String)

@Serializable
data class StatsResponse(
    val originalUrl: String,
    val clickCount: Int,
    val createdAt: String,
    val isActive: Boolean
)

fun Route.urlShortenerRoutes() {
    route("/api") {
        post("/shorten") {
            val req = try {
                call.receive<ShortenRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request body"))
                return@post
            }

            // Validate custom alias if provided
            if (req.customAlias != null) {
                if (!Regex("^[a-zA-Z0-9]{6,7}$").matches(req.customAlias)) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Custom alias must be 6-7 alphanumeric characters"))
                    return@post
                }
                // Check alias availability
                val exists = UrlService.getUrlByShortCode(req.customAlias)
                if (exists != null) {
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to "Custom alias is already taken"))
                    return@post
                }
            }

            val result = UrlService.createShortUrl(
                CreateUrlRequest(
                    originalUrl = req.url,
                    expiresAt = null // You can extend to support this
                )
            )

            when (result) {
                is UrlShortenResult.Success -> {
                    val baseUrl = "${call.request.local.scheme}://${call.request.local.localHost}${if (call.request.local.localPort != 80 && call.request.local.localPort != 443) ":${call.request.local.localPort}" else ""}"
                    call.respond(
                        ShortenResponse(
                            shortUrl = "$baseUrl/${result.url.shortCode}",
                            shortCode = result.url.shortCode
                        )
                    )
                }
                is UrlShortenResult.InvalidUrl -> call.respond(HttpStatusCode.BadRequest, mapOf("error" to result.reason))
                is UrlShortenResult.AlreadyShortened -> call.respond(HttpStatusCode.BadRequest, mapOf("error" to "URL is already shortened"))
                is UrlShortenResult.CollisionError -> call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to generate unique short code"))
                else -> call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Unknown error"))
            }
        }

        get("/stats/{shortCode}") {
            val code = call.parameters["shortCode"] ?: ""
            val url = UrlService.getUrlByShortCode(code)
            if (url == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Short URL not found"))
                return@get
            }
            call.respond(
                StatsResponse(
                    originalUrl = url.originalUrl,
                    clickCount = url.clickCount,
                    createdAt = url.createdAt.toString(),
                    isActive = url.isActive
                )
            )
        }

        // Optional admin endpoint
        get("/urls") {
            // For demo: return all URLs (pagination/search/filter can be added)
            val urls = UrlService.getAllUrls()
            call.respond(urls)
        }
    }

    // Redirect endpoint
    get("/{shortCode}") {
        val code = call.parameters["shortCode"] ?: ""
        val url = UrlService.getUrlByShortCode(code)
        if (url == null || !url.isActive) {
            call.respond(HttpStatusCode.NotFound, mapOf("error" to "Short URL not found or inactive"))
            return@get
        }
        if (url.expiresAt != null && url.expiresAt.isBefore(java.time.Instant.now())) {
            call.respond(HttpStatusCode.Gone, mapOf("error" to "Short URL has expired"))
            return@get
        }
        UrlService.incrementClickCountAndLog(code)
        call.respondRedirect(url.originalUrl, permanent = true)
    }
}