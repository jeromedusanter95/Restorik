package com.jeromedusanter.restorik.core.database.model

import androidx.room.ColumnInfo

/**
 * Query result for restaurant spending aggregation.
 * Used by MealDao.getTopRestaurantsBySpendingForMonth()
 */
data class RestaurantSpendingResult(
    @ColumnInfo(name = "restaurant_id") val restaurantId: Int,
    @ColumnInfo(name = "total_spending") val totalSpending: Double
)
