package com.jeromedusanter.restorik.feature.meal.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jeromedusanter.restorik.feature.meal.R

@Composable
fun FilterSortDialog(
    currentSortMode: SortMode,
    currentSortOrder: SortOrder,
    onDismiss: () -> Unit,
    onApply: (SortMode, SortOrder) -> Unit,
) {
    var selectedSortMode by remember { mutableStateOf(currentSortMode) }
    var selectedSortOrder by remember { mutableStateOf(currentSortOrder) }

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = onDismiss,
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.feature_meal_filter_dialog_title),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column {
                // Sort By section
                Text(
                    text = stringResource(R.string.feature_meal_filter_sort_by_label),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Column(modifier = Modifier.selectableGroup()) {
                    SortModeOption(
                        text = stringResource(R.string.feature_meal_filter_sort_by_date),
                        selected = selectedSortMode == SortMode.DATE,
                        onClick = { selectedSortMode = SortMode.DATE }
                    )
                    SortModeOption(
                        text = stringResource(R.string.feature_meal_filter_sort_by_restaurant),
                        selected = selectedSortMode == SortMode.RESTAURANT,
                        onClick = { selectedSortMode = SortMode.RESTAURANT }
                    )
                    SortModeOption(
                        text = stringResource(R.string.feature_meal_filter_sort_by_rating),
                        selected = selectedSortMode == SortMode.RATING,
                        onClick = { selectedSortMode = SortMode.RATING }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Order section
                Text(
                    text = stringResource(R.string.feature_meal_filter_order_label),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Column(modifier = Modifier.selectableGroup()) {
                    SortOrderOption(
                        text = stringResource(R.string.feature_meal_filter_order_ascending),
                        selected = selectedSortOrder == SortOrder.ASCENDING,
                        onClick = { selectedSortOrder = SortOrder.ASCENDING }
                    )
                    SortOrderOption(
                        text = stringResource(R.string.feature_meal_filter_order_descending),
                        selected = selectedSortOrder == SortOrder.DESCENDING,
                        onClick = { selectedSortOrder = SortOrder.DESCENDING }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onApply(selectedSortMode, selectedSortOrder)
                    onDismiss()
                }
            ) {
                Text(text = stringResource(R.string.feature_meal_filter_apply_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.feature_meal_filter_cancel_button))
            }
        }
    )
}

@Composable
private fun SortModeOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
private fun SortOrderOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
