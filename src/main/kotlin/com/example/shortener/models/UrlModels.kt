package com.example.shortener.models

import kotlinx.serialization.Serializable
import java.time.Instant
import kotlinx.serialization.Contextual

@Serializable
data class CreateUrlRequest(
    val originalUrl: String,
    @Contextual
    val expiresAt: Instant? = null
)

@Serializable
data class UrlResponse(
    val id: Int,
    val originalUrl: String,
    val shortCode: String,
    @Contextual
    val createdAt: Instant,
    val clickCount: Int,
    @Contextual
    val expiresAt: Instant? = null,
    val isActive: Boolean
)