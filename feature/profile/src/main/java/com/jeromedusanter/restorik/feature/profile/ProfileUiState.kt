package com.jeromedusanter.restorik.feature.profile

import java.time.YearMonth

data class ProfileUiState(
    val selectedMonth: YearMonth,
    val monthlySpending: Double,
    val previousMonthSpending: Double,
    val numberOfMeals: Int,
    val averageRating: Double,
    val numberOfRestaurants: Int,
    val newRestaurantsTried: Int,
    val topRestaurantsBySpending: List<RestaurantSpending>,
    val averageMealSpending: Double,
    val isLoading: Boolean
) {
    companion object {
        val EMPTY = ProfileUiState(
            selectedMonth = YearMonth.now(),
            monthlySpending = 0.0,
            previousMonthSpending = 0.0,
            numberOfMeals = 0,
            averageRating = 0.0,
            numberOfRestaurants = 0,
            newRestaurantsTried = 0,
            topRestaurantsBySpending = emptyList(),
            averageMealSpending = 0.0,
            isLoading = true
        )
    }
}
