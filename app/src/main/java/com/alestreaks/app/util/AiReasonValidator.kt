package com.alestreaks.app.util

class AiReasonValidator {
    fun classify(reason: String): String {
        val text = reason.trim().lowercase()
        if (text.length < 8) return "weak"
        val validHints = listOf("sick", "travel", "emergency", "injury", "work", "family")
        return if (validHints.any { text.contains(it) }) "valid" else "unknown"
    }
}
