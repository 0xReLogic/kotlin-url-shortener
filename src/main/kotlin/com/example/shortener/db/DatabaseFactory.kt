package com.example.shortener.db

import com.typesafe.config.ConfigFactory
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

object DatabaseFactory {
    fun init() {
        val config = ConfigFactory.load()
        val dbConfig = config.getConfig("exposed.db")
        val url = dbConfig.getString("url")
        val driver = dbConfig.getString("driver")
        val user = dbConfig.getString("user")
        val password = dbConfig.getString("password")

        Database.connect(
            url = url,
            driver = driver,
            user = user,
            password = password,
            databaseConfig = DatabaseConfig {
                defaultIsolationLevel = Connection.TRANSACTION_REPEATABLE_READ
            }
        )

        // Run migration
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(UrlsTable, ClickAnalyticsTable)
        }
    }
}