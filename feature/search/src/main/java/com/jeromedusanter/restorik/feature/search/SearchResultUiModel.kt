package com.jeromedusanter.restorik.feature.search

sealed class SearchResultUiModel {
    data class MealItem(
        val id: Int,
        val name: String,
        val restaurantName: String,
        val rating: Int,
        val photoUri: String?,
    ) : SearchResultUiModel()

    data class RestaurantItem(
        val id: Int,
        val name: String,
    ) : SearchResultUiModel()
}
