package com.jeromedusanter.restorik.core.data

import com.jeromedusanter.restorik.core.model.Meal
import kotlinx.coroutines.flow.Flow

interface MealRepository {
    fun observeMealById(id: Int): Flow<Meal>
    fun observeAll(): Flow<List<Meal>>
    suspend fun saveMealInLocalDb(meal: Meal)
}