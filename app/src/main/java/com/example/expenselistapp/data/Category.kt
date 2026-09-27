package com.example.expenselistapp.data

enum class Category(
    val label: String,
) {
    Food("Food"),
    Transport("Transport"),
    Housing("Housing"),
    Leisure("Leisure"),
    Other("Other"),
    ;

    companion object {
        val Default: Category = Other
    }
}
