package com.example.shortener.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object ClickAnalyticsTable : Table("click_analytics") {
    val id = integer("id").autoIncrement()
    val urlId = integer("url_id").references(UrlsTable.id)
    val clickedAt = timestamp("clicked_at")

    override val primaryKey = PrimaryKey(id)
}