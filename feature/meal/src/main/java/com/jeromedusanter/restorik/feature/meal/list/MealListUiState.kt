package com.jeromedusanter.restorik.feature.meal.list

import androidx.compose.runtime.Immutable

@Immutable
data class MealListUiState(
    val groupedMealList: List<MealGroupUiModel>,
    val isLoading: Boolean
) {
    companion object {
        val EMPTY = MealListUiState(
            groupedMealList = emptyList(),
            isLoading = true,
        )
    }
}