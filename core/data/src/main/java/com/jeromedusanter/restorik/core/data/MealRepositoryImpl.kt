package com.jeromedusanter.restorik.core.data

import com.jeromedusanter.restorik.core.database.dao.MealDao
import com.jeromedusanter.restorik.core.database.model.MealEntity
import com.jeromedusanter.restorik.core.model.Meal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MealRepositoryImpl @Inject constructor(
    private val mealDao: MealDao,
    private val mealMapper: MealMapper
) : MealRepository {

    override fun observeMealById(id: Int): Flow<Meal> {
        TODO("Not yet implemented")
    }

    override fun observeAll(): Flow<List<Meal>> {
        return mealDao.getAll().map { list ->
            list.map { mealMapper.mapEntityModelToDomainModel(it) }
        }
    }

    override suspend fun saveMealInLocalDb(meal: Meal) {
        mealDao.upsert(
            MealEntity(
                id = meal.id,
                name = meal.name,
                comment = meal.comment,
                price = meal.price,
                restaurantId = meal.restaurantId,
                dateTime = meal.dateTime.toString(),
                ratingOnFive = meal.ratingOnFive,
                photoList = meal.photoList,
            )
        )
    }
}