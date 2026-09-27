package com.example.expenselistapp.data

import java.math.BigDecimal

data class Expense(
    val amount: BigDecimal,
    val category: Category,
    val comment: String?,
    val sequence: Int,
)
