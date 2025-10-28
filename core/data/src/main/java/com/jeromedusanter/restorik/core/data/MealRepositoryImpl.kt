package com.jeromedusanter.restorik.core.data

import com.jeromedusanter.restorik.core.model.Meal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MealRepositoryImpl @Inject constructor() : MealRepository {

    override fun observeMealById(id: Int): Flow<Meal> {
        TODO("Not yet implemented")
    }

    override fun observeAll(): Flow<List<Meal>> {
        TODO("Not yet implemented")
    }
}