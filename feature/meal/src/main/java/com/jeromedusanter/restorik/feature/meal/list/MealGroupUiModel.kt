package com.jeromedusanter.restorik.feature.meal.list

import androidx.compose.runtime.Immutable

@Immutable
data class MealGroupUiModel(
    val title: String,
    val mealList: List<MealUiModel>,
    val ratingValue: Int? = null
)
