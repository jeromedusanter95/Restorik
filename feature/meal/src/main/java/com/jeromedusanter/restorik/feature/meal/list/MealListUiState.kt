package com.jeromedusanter.restorik.feature.meal.list

import androidx.compose.runtime.Immutable

@Immutable
data class MealListUiState(
    val groupedMealList: List<MealGroupUiModel>,
    val isLoading: Boolean,
    val sortMode: SortMode,
    val sortOrder: SortOrder,
    val showFilterDialog: Boolean,
    val filterRestaurantName: String? = null,
) {
    companion object {
        val EMPTY = MealListUiState(
            groupedMealList = emptyList(),
            isLoading = true,
            sortMode = SortMode.DATE,
            sortOrder = SortOrder.DESCENDING,
            showFilterDialog = false,
            filterRestaurantName = null,
        )
    }
}