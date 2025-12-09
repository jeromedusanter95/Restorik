package com.jeromedusanter.restorik.feature.meal.detail

import android.net.Uri

data class MealDetailUiState(
    val restaurantName: String,
    val name: String,
    val comment: String,
    val priceAsString: String,
    val ratingOnFive: Int,
    val photoUriList: List<Uri>,
) {
    companion object {
        val EMPTY = MealDetailUiState(
            name = "",
            restaurantName = "",
            comment = "",
            priceAsString = "0.0",
            ratingOnFive = 0,
            photoUriList = emptyList(),
        )
    }
}