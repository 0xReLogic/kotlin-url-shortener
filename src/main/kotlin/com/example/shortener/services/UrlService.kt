package com.example.shortener.services

import com.example.shortener.db.UrlsTable
import com.example.shortener.db.ClickAnalyticsTable
import com.example.shortener.models.CreateUrlRequest
import com.example.shortener.models.UrlResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI
import java.net.URISyntaxException
import java.time.Instant
import java.util.*
import java.util.regex.Pattern

sealed class UrlShortenResult {
    data class Success(val url: UrlResponse) : UrlShortenResult()
    data class InvalidUrl(val reason: String) : UrlShortenResult()
    data class AlreadyShortened(val shortCode: String) : UrlShortenResult()
    object CollisionError : UrlShortenResult()
    object UnknownError : UrlShortenResult()
}

object UrlService {
    private val base62Chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    private const val SHORT_CODE_LENGTH = 6
    private val urlPattern = Pattern.compile(
        "^(https?://)" + // protocol
        "([\\w\\-]+\\.)+[\\w\\-]+" + // domain
        "(:\\d+)?(/\\S*)?$", // port and path
        Pattern.CASE_INSENSITIVE
    )
    private val serviceDomain = "short.ly" // Ganti dengan domain shortener Anda

    fun isValidUrl(url: String): Boolean {
        if (url.length > 2000) return false
        if (!urlPattern.matcher(url).matches()) return false
        return try {
            val uri = URI(url)
            uri.scheme == "http" || uri.scheme == "https"
        } catch (e: URISyntaxException) {
            false
        }
    }

    fun isAlreadyShortened(url: String): Boolean {
        return try {
            val uri = URI(url)
            uri.host?.contains(serviceDomain, ignoreCase = true) ?: false
        } catch (e: Exception) {
            false
        }
    }

    fun sanitizeUrl(url: String): String {
        // Remove leading/trailing spaces and control characters
        return url.trim().replace(Regex("[\\p{Cntrl}]"), "")
    }

    fun generateBase62Code(): String {
        return (1..SHORT_CODE_LENGTH)
            .map { base62Chars.random() }
            .joinToString("")
    }

    suspend fun createShortUrl(request: CreateUrlRequest): UrlShortenResult = withContext(Dispatchers.IO) {
        val sanitizedUrl = sanitizeUrl(request.originalUrl)
        if (!isValidUrl(sanitizedUrl)) return@withContext UrlShortenResult.InvalidUrl("Invalid URL format")
        if (isAlreadyShortened(sanitizedUrl)) return@withContext UrlShortenResult.AlreadyShortened("URL is already shortened")

        val now = Instant.now()
        var code: String
        var retry = 0
        val maxRetry = 5
        var id: Int? = null

        while (retry < maxRetry) {
            code = generateBase62Code()
            try {
                id = transaction {
                    // Check for collision
                    if (UrlsTable.select { UrlsTable.shortCode eq code }.count() > 0) {
                        throw Exception("Collision")
                    }
                    val insertStatement = UrlsTable.insert {
                        it[originalUrl] = sanitizedUrl
                        it[shortCode] = code
                        it[createdAt] = now
                        it[clickCount] = 0
                        it[expiresAt] = request.expiresAt
                        it[isActive] = true
                    }
                    insertStatement.resultedValues?.firstOrNull()?.get(UrlsTable.id)
                }
                break
            } catch (e: Exception) {
                retry++
                if (retry == maxRetry) return@withContext UrlShortenResult.CollisionError
            }
        }

        id?.let {
            getUrlById(it)?.let { url -> UrlShortenResult.Success(url) } ?: UrlShortenResult.UnknownError
        } ?: UrlShortenResult.UnknownError
    }

    suspend fun getUrlById(id: Int): UrlResponse? = withContext(Dispatchers.IO) {
        transaction {
            UrlsTable.select { UrlsTable.id eq id }
                .map { toUrlResponse(it) }
                .singleOrNull()
        }
    }

    suspend fun getUrlByShortCode(code: String): UrlResponse? = withContext(Dispatchers.IO) {
        transaction {
            UrlsTable.select { UrlsTable.shortCode eq code }
                .map { toUrlResponse(it) }
                .singleOrNull()
        }
    }

    suspend fun incrementClickCountAndLog(code: String): Boolean = withContext(Dispatchers.IO) {
        transaction {
            val urlRow = UrlsTable.select { UrlsTable.shortCode eq code }.singleOrNull()
            if (urlRow != null) {
                val urlId = urlRow[UrlsTable.id]
                UrlsTable.update({ UrlsTable.id eq urlId }) {
                    with(SqlExpressionBuilder) {
                        it.update(UrlsTable.clickCount, UrlsTable.clickCount + 1)
                    }
                }
                ClickAnalyticsTable.insert {
                    it[this.urlId] = urlId
                    it[this.clickedAt] = Instant.now()
                }
                true
            } else {
                false
            }
        }
    }

    suspend fun deactivateUrl(id: Int) = withContext(Dispatchers.IO) {
        transaction {
            UrlsTable.update({ UrlsTable.id eq id }) {
                it[isActive] = false
            }
        }
    }

    private fun toUrlResponse(row: ResultRow): UrlResponse =
        UrlResponse(
            id = row[UrlsTable.id],
            originalUrl = row[UrlsTable.originalUrl],
            shortCode = row[UrlsTable.shortCode],
            createdAt = row[UrlsTable.createdAt],
            clickCount = row[UrlsTable.clickCount],
            expiresAt = row[UrlsTable.expiresAt],
            isActive = row[UrlsTable.isActive]
        )

    suspend fun getAllUrls(): List<UrlResponse> = withContext(Dispatchers.IO) {
        transaction {
            UrlsTable.selectAll().map { toUrlResponse(it) }
        }
    }
}