package com.example.expenselistapp.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.expenselistapp.data.Category
import com.example.expenselistapp.data.Money
import java.math.BigDecimal

private const val MAX_COMMENT_LENGTH = 120

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddExpenseSheet(
    onDismiss: () -> Unit,
    onConfirm: (amount: BigDecimal, category: Category, comment: String?) -> Unit,
) {
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(Category.Default) }
    var comment by remember { mutableStateOf("") }

    var showAmountError by remember { mutableStateOf(false) }
    val amountError = remember(showAmountError, amount) {
        if (showAmountError && Money.parse(amount) == null) INVALID_AMOUNT_MESSAGE else null
    }

    val keyboard = LocalSoftwareKeyboardController.current

    fun submit() {
        val parsed = Money.parse(amount)
        if (parsed == null) {
            showAmountError = true
            return
        }
        keyboard?.hide()
        onConfirm(parsed, category, comment.takeIf { it.isNotBlank() })
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(text = "New expense", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ExpenseTestTags.AmountField),
                label = { Text("Amount") },
                placeholder = { Text("0.00") },
                singleLine = true,
                isError = amountError != null,
                supportingText = amountError?.let { message ->
                    { Text(message) }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { submit() }),
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelLarge,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Category.entries.forEach { option ->
                        FilterChip(
                            selected = category == option,
                            onClick = { category = option },
                            label = { Text(option.label) },
                            modifier = Modifier.testTag(ExpenseTestTags.categoryChip(option)),
                        )
                    }
                }
            }

            OutlinedTextField(
                value = comment,
                onValueChange = { input ->
                    comment = input.take(MAX_COMMENT_LENGTH)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ExpenseTestTags.CommentField),
                label = { Text("Comment (optional)") },
                singleLine = false,
                minLines = 1,
                maxLines = 3,
                supportingText = { Text("${comment.length}/$MAX_COMMENT_LENGTH") },
            )

            Button(
                onClick = { submit() },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ExpenseTestTags.SaveButton),
            ) {
                Text("Save expense")
            }
        }
    }
}
private const val INVALID_AMOUNT_MESSAGE =
    "Enter an amount between 0.01 and 1,000,000. Use . or , as the decimal separator."
