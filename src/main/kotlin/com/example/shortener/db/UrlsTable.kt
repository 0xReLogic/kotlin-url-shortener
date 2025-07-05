package com.example.shortener.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object UrlsTable : Table("urls") {
    val id = integer("id").autoIncrement()
    val originalUrl = varchar("original_url", 2000)
    val shortCode = varchar("short_code", 7).uniqueIndex()
    val createdAt = timestamp("created_at")
    val clickCount = integer("click_count").default(0)
    val expiresAt = timestamp("expires_at").nullable()
    val isActive = bool("is_active").default(true)

    override val primaryKey = PrimaryKey(id)
}