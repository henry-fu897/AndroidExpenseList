package com.example.expenselistapp.data

import androidx.compose.runtime.mutableStateListOf
import java.math.BigDecimal

class ExpenseStore {
    private val entries = mutableStateListOf<Expense>()

    val all: List<Expense> get() = entries

    private var nextSequence = 0

    fun add(amount: BigDecimal, category: Category, comment: String?): Expense {
        val expense = Expense(
            amount = amount,
            category = category,
            comment = comment?.trim()?.takeIf { it.isNotEmpty() },
            sequence = nextSequence++,
        )
        entries.add(0, expense)
        return expense
    }

    fun visible(filter: Category?, limit: Int = RECENT_LIMIT): List<Expense> =
        entries.asSequence()
            .filter { filter == null || it.category == filter }
            .sortedByDescending { it.sequence }
            .take(limit)
            .toList()

    fun countMatching(filter: Category?): Int =
        if (filter == null) entries.size else entries.count { it.category == filter }

    fun totalMatching(filter: Category?): BigDecimal =
        entries.asSequence()
            .filter { filter == null || it.category == filter }
            .fold(BigDecimal.ZERO) { acc, expense -> acc + expense.amount }


    companion object {
        /** The app is a "recent activity" view, not a full history. */
        const val RECENT_LIMIT: Int = 5
    }
}