package com.example.expenselistapp.data

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

object Money {
    val SCALE: Int = 2

    val MINIMUM: BigDecimal = BigDecimal("0.01")
    val MAXIMUM: BigDecimal = BigDecimal("1000000.00")

    fun parse(raw: String): BigDecimal? {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null

        if (trimmed.any { it == '-' || it == '\u2212' }) return null

        val normalized = normalizeSeparators(trimmed) ?: return null
        val parsed = try {
            BigDecimal(normalized)
        } catch (_: NumberFormatException) {
            return null
        }

        val rounded = parsed.setScale(SCALE, RoundingMode.HALF_UP)
        if (rounded < MINIMUM || rounded > MAXIMUM) return null
        return rounded
    }

    // Make sure that string input is normalized
    private fun normalizeSeparators(input: String): String? {
        val lastSeparator = input.indexOfLast { it == '.' || it == ',' }
        if (lastSeparator < 0) {
            val digits = input.filter(Char::isDigit)
            return digits.ifEmpty { null }
        }

        val whole = input.substring(0, lastSeparator).filter(Char::isDigit)
        val fraction = input.substring(lastSeparator + 1).filter(Char::isDigit)
        if (whole.isEmpty() && fraction.isEmpty()) return null

        return buildString {
            append(if (whole.isEmpty()) "0" else whole)
            if (fraction.isNotEmpty()) {
                append('.')
                append(fraction)
            }
        }
    }

    fun format(value: BigDecimal, locale: Locale = Locale.getDefault()): String =
        NumberFormat.getCurrencyInstance(locale).format(value)
}