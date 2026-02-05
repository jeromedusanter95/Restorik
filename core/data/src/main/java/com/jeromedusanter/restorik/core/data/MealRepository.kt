package com.jeromedusanter.restorik.core.data

import com.jeromedusanter.restorik.core.model.Meal
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth

interface MealRepository {
    suspend fun getById(id: Int): Meal?
    fun observeMealById(id: Int): Flow<Meal>
    fun observeAll(): Flow<List<Meal>>
    suspend fun saveMealInLocalDb(meal: Meal)
    suspend fun deleteMeal(id: Int)
    fun getTotalSpendingForMonth(yearMonth: String): Flow<Double>
    fun getMealCountForMonth(yearMonth: String): Flow<Int>
    fun getUniqueRestaurantCountForMonth(yearMonth: String): Flow<Int>
    fun getAverageRatingForMonth(yearMonth: String): Flow<Double>
    fun getFirstMealYearMonth(): Flow<YearMonth?>
    fun getTopRestaurantIdsBySpendingForMonth(yearMonth: String, limit: Int): Flow<List<Pair<Int, Double>>>
    fun getNewRestaurantsTriedCount(selectedYearMonth: String): Flow<Int>
}