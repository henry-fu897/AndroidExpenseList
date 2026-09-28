package com.example.expenselistapp.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.expenselistapp.data.Category
import com.example.expenselistapp.data.ExpenseStore
import com.example.expenselistapp.data.Money
import com.example.expenselistapp.data.Expense
import java.math.BigDecimal

object ExpenseTestTags {
    const val AddButton = "add_expense_button"
    const val AmountField = "amount_field"
    const val CommentField = "comment_field"
    const val SaveButton = "save_expense_button"
    const val ClearFilter = "clear_filter"
    const val EmptyState = "empty_state"
    const val ExpenseRow = "expense_row"

    /** Distinct tag per filter chip, so tests never match the sheet's chips. */
    fun filterChip(category: Category?): String =
        "filter_chip_${category?.name ?: "ALL"}"

    fun categoryChip(category: Category): String = "sheet_category_${category.name}"
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExpenseScreen(
    store: ExpenseStore,
    modifier: Modifier = Modifier,
    onAddExpense: (amount: BigDecimal, category: Category, comment: String?) -> Unit,
) {

    var filter: Category? by remember { mutableStateOf(null) }
    var showAddSheet by remember { mutableStateOf(false) }

    val visible = store.visible(filter)
    val matchingCount = store.countMatching(filter)
    val total = store.totalMatching(filter)

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
            ) {

            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            SummaryCard(
                total = total,
                matchingCount = matchingCount,
                visibleCount = visible.size,
                filter = filter,
            )

            CategoryFilterRow(
                selected = filter,
                onSelect = { filter = it },
            )

            if (visible.isEmpty()) {
                EmptyState(filter = filter)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 96.dp),
                ) {
                    items(items = visible, key = { it.sequence }) { expense ->
                        ExpenseRow(expense)
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    if (showAddSheet) {
        AddExpenseSheet(
            onDismiss = { showAddSheet = false },
            onConfirm = { amount, category, comment ->
                onAddExpense(amount, category, comment)
                showAddSheet = false
            },
        )
    }
}

@Composable
private fun SummaryCard(
    total: BigDecimal,
    matchingCount: Int,
    visibleCount: Int,
    filter: Category?,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = filter?.let { "Total · ${it.label}" } ?: "Total · all categories",
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = Money.format(total),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = buildString {
                    append(if (matchingCount == 1) "1 expense" else "$matchingCount expenses")
                    if (visibleCount < matchingCount) {
                        append(" · showing the latest $visibleCount")
                    }
                },
                style = MaterialTheme.typography.bodySmall,
            )
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
private fun ExpenseRow(expense: Expense) {
    ListItem(
        modifier = Modifier.testTag(ExpenseTestTags.ExpenseRow),
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
        headlineContent = {
            Text(
                text = expense.comment ?: expense.category.label,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
            )
        },
        supportingContent = {
            Text(
                text = buildString {
                    if (expense.comment != null) {
                        append(expense.category.label)
                        append(" · ")
                    }
                },
                style = MaterialTheme.typography.bodySmall,
            )
        },
        trailingContent = {
            Text(
                text = Money.format(expense.amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        },
    )
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
                "Tap the button corner right to record your first expense."
            } else {
                "Nothing has been recorded in this category. Tap \"All\" to see your other expenses."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}