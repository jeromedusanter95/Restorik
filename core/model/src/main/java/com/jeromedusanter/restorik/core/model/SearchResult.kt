package com.jeromedusanter.restorik.core.model

sealed class SearchResult {
    data class MealResult(val meal: Meal, val restaurantName: String) : SearchResult()
    data class RestaurantResult(val restaurant: Restaurant) : SearchResult()
}
