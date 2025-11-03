package com.jeromedusanter.restorik.feature.meal.list

import androidx.compose.runtime.Immutable

@Immutable
data class MealListUiState(
    val mealList: List<MealUiModel>,
    val isLoading: Boolean
) {
    companion object {
        val EMPTY = MealListUiState(
            mealList = emptyList(),
            isLoading = false,
        )
    }
}