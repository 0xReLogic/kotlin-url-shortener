package com.example.shortener.db

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

object SeedData {
    fun insertSampleUrls() {
        transaction {
            UrlsTable.insert {
                it[originalUrl] = "https://kotlinlang.org/"
                it[shortCode] = "kot123"
                it[createdAt] = Instant.now()
                it[clickCount] = 0
                it[expiresAt] = null
                it[isActive] = true
            }
            UrlsTable.insert {
                it[originalUrl] = "https://ktor.io/"
                it[shortCode] = "ktorio"
                it[createdAt] = Instant.now()
                it[clickCount] = 0
                it[expiresAt] = null
                it[isActive] = true
            }
        }
    }
}