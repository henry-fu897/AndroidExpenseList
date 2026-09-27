package com.example.expenselistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.example.expenselistapp.data.ExpenseStore
import com.example.expenselistapp.ui.theme.ExpenseListAppTheme
import com.example.expenselistapp.ui.theme.ExpenseScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseListAppTheme {
                val store = remember { (ExpenseStore()) }

                ExpenseScreen(
                    store = store,
                    onAddExpense = { amount, category, comment ->
                        store.add(amount, category, comment)
                    },
                )
            }
        }
    }
}