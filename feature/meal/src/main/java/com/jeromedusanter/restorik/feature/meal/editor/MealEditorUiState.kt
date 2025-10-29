package com.jeromedusanter.restorik.feature.meal.editor

import androidx.compose.runtime.Immutable

@Immutable
data class MealEditorUiState(
    val id: Int,
    val restaurantName: String,
    val name: String,
    val comment: String,
    val priceAsString: String,
    val ratingOnFive: Int,
    val picturePathList: List<String>
) {
    companion object {
        val EMPTY = MealEditorUiState(
            id = -1,
            restaurantName = "",
            name = "",
            comment = "",
            priceAsString = "0.0",
            ratingOnFive = 0,
            picturePathList = emptyList()
        )
    }
}