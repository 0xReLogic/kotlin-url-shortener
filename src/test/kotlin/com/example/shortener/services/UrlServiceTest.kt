package com.example.shortener.services

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UrlServiceTest {

    @Test
    fun `valid URLs pass validation`() {
        val validUrls = listOf(
            "http://example.com",
            "https://example.com",
            "https://sub.domain.com/path?query=1",
            "http://localhost:8080/test"
        )
        validUrls.forEach {
            assertTrue(UrlService.isValidUrl(it), "Should be valid: $it")
        }
    }

    @Test
    fun `invalid URLs fail validation`() {
        val invalidUrls = listOf(
            "ftp://example.com",
            "example.com",
            "http:/example.com",
            "https://",
            "javascript:alert(1)",
            "http://"
        )
        invalidUrls.forEach {
            assertFalse(UrlService.isValidUrl(it), "Should be invalid: $it")
        }
    }

    @Test
    fun `short code is base62 and correct length`() {
        repeat(20) {
            val code = UrlService.generateBase62Code()
            assertTrue(code.matches(Regex("^[a-zA-Z0-9]{6}$")), "Short code: $code")
        }
    }

    @Test
    fun `sanitizeUrl removes control characters and trims`() {
        val dirty = " \n\thttp://example.com\r\n"
        val clean = UrlService.sanitizeUrl(dirty)
        assertEquals("http://example.com", clean)
    }
}