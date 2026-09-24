package com.example.data.util

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Locale
import java.util.regex.Pattern
import kotlin.random.Random

object SecurityUtils {
    private val EMAIL_PATTERN = Pattern.compile(
        "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
        Pattern.CASE_INSENSITIVE
    )

    fun generateSalt(): String {
        val random = SecureRandom()
        val saltBytes = ByteArray(16)
        random.nextBytes(saltBytes)
        return saltBytes.joinToString("") { "%02x".format(it) }
    }

    fun hashPassword(password: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val input = salt + password
        val digest = md.digest(input.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(password: String, salt: String, expectedHash: String): Boolean {
        val actualHash = hashPassword(password, salt)
        return actualHash.equals(expectedHash, ignoreCase = true)
    }

    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        return EMAIL_PATTERN.matcher(email.trim()).matches()
    }

    fun isValidMobile(mobile: String): Boolean {
        val trimmed = mobile.trim()
        // Standard 10-digit mobile number in India starting with 6-9
        return trimmed.length == 10 && trimmed.all { it.isDigit() } && (trimmed.startsWith("6") || trimmed.startsWith("7") || trimmed.startsWith("8") || trimmed.startsWith("9"))
    }

    fun isValidEmployeeId(employeeId: String): Boolean {
        val trimmed = employeeId.trim()
        return trimmed.length >= 3
    }

    fun generateRecordId(employeeType: String): String {
        val prefix = if (employeeType.uppercase().contains("DRIVER")) "DRV" else "CND"
        val timestamp = System.currentTimeMillis() % 100000
        val random = Random.nextInt(100, 999)
        return "UPSRTC-$prefix-$timestamp-$random"
    }
}
