package com.jeromedusanter.restorik.core.model

sealed class SearchResult {
    data class MealResult(
        val meal: Meal,
        val restaurantName: String,
        val cityName: String
    ) : SearchResult()

    data class RestaurantResult(
        val restaurant: Restaurant,
        val cityName: String
    ) : SearchResult()
}
