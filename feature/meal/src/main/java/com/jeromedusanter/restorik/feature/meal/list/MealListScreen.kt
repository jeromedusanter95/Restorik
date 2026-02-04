package com.jeromedusanter.restorik.feature.meal.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme
import com.jeromedusanter.restorik.feature.meal.R
import java.util.Locale

@Composable
fun MealListScreen(
    modifier: Modifier = Modifier,
    uiState: MealListUiState,
    onCloseRestaurantFilter: () -> Unit = {},
    onClickMealItem: (Int) -> Unit = {},
    onDismissFilterDialog: () -> Unit = {},
    onApplyFilter: (SortMode, SortOrder) -> Unit = { _, _ -> },
) {
    Column(modifier = Modifier.fillMaxSize()) {

        if (!uiState.filterRestaurantName.isNullOrEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Start,
            ) {
                FilterChip(
                    selected = true,
                    onClick = onCloseRestaurantFilter,
                    label = { Text(text = uiState.filterRestaurantName) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.feature_meal_clear_filter),
                            modifier = Modifier.size(size = 18.dp)
                        )
                    }
                )
            }
        }

        when {
            uiState.isLoading -> {
                MealListLoadingState()
            }

            uiState.groupedMealList.isEmpty() -> {
                MealListEmptyState()
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(all = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(space = 8.dp)
                ) {
                    uiState.groupedMealList.forEach { group ->
                        item(key = "header_${group.title}_${group.ratingValue}") {
                            if (group.ratingValue != null) {
                                // Display rating with star icon
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp, bottom = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = String.format(Locale.getDefault(), "%.1f", group.ratingValue),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            } else {
                                // Display text for date/restaurant groups
                                Text(
                                    text = group.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp, bottom = 4.dp)
                                )
                            }
                        }

                        items(
                            items = group.mealList,
                            key = { meal -> meal.id }
                        ) { meal ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInVertically(
                                    initialOffsetY = { it / 2 }
                                )
                            ) {
                                MealListItem(
                                    mealUiModel = meal,
                                    onClickItem = { onClickMealItem(meal.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (uiState.showFilterDialog) {
        FilterSortDialog(
            currentSortMode = uiState.sortMode,
            currentSortOrder = uiState.sortOrder,
            onDismiss = onDismissFilterDialog,
            onApply = onApplyFilter
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MealListScreenPreview() {
    RestorikTheme {
        MealListScreen(uiState = MealListUiState.EMPTY)
    }
}