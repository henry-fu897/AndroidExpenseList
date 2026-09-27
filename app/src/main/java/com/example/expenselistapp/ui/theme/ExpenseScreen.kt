package com.example.expenselistapp.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.expenselistapp.data.Category
import com.example.expenselistapp.data.ExpenseStore
import java.math.BigDecimal

object ExpenseTestTags {
    const val AddButton = "add_expense_button"
    const val ClearFilter = "clear_filter"
    const val EmptyState = "empty_state"
    fun filterChip(category: Category?): String =
        "filter_chip_${category?.name ?: "ALL"}"

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExpenseScreen(
    store: ExpenseStore,
    modifier: Modifier = Modifier,
    onAddExpense: (amount: BigDecimal, category: Category, comment: String?) -> Unit, ) {

    var filter: Category? by remember { mutableStateOf(null) }
    var showAddSheet by remember { mutableStateOf(false) }

    val visible = store.visible(filter)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("My expenses") },
                actions = {
                    if (filter != null) {
                        IconButton(
                            onClick = { filter = null },
                            modifier = Modifier.testTag(ExpenseTestTags.ClearFilter),
                        ) {}
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                modifier = Modifier.testTag(ExpenseTestTags.AddButton),

            ) {}
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {

            CategoryFilterRow(
                selected = filter,
                onSelect = { filter = it },
            )

            if (visible.isEmpty()) {
                EmptyState(filter = filter)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryFilterRow(
    selected: Category?,
    onSelect: (Category?) -> Unit,
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = selected == null,
            onClick = { onSelect(null) },
            label = { Text("All") },
            modifier = Modifier.testTag(ExpenseTestTags.filterChip(null)),
        )
        Category.entries.forEach { category ->
            FilterChip(
                selected = selected == category,
                onClick = {
                    onSelect(if (selected == category) null else category)
                },
                label = { Text(category.label) },
                modifier = Modifier.testTag(ExpenseTestTags.filterChip(category)),
            )
        }
    }
}

@Composable
private fun EmptyState(filter: Category?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .testTag(ExpenseTestTags.EmptyState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = if (filter == null) "No expenses yet" else "No ${filter.label} expenses",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (filter == null) {
                "Tap the button to record your first expense."
            } else {
                "Nothing has been recorded in this category. Tap \"All\" to see your other expenses."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

    }
}