package com.jeromedusanter.restorik.core.data

import com.jeromedusanter.restorik.core.database.dao.DishDao
import com.jeromedusanter.restorik.core.database.dao.MealDao
import com.jeromedusanter.restorik.core.database.model.MealEntity
import com.jeromedusanter.restorik.core.model.Meal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class MealRepositoryImpl @Inject constructor(
    private val mealDao: MealDao,
    private val dishDao: DishDao,
    private val mealMapper: MealMapper,
    private val dishMapper: DishMapper
) : MealRepository {

    override fun observeMealById(id: Int): Flow<Meal> {
        return mealDao.observeByIdWithDishes(id = id).mapNotNull { mealWithDishes ->
            mealWithDishes?.let { mealMapper.mapEntityModelToDomainModel(mealWithDishes = it) }
        }
    }

    override fun observeAll(): Flow<List<Meal>> {
        return mealDao.observeAllWithDishes().map { list ->
            list.map { mealMapper.mapEntityModelToDomainModel(mealWithDishes = it) }
        }
    }

    override suspend fun saveMealInLocalDb(meal: Meal) {
        val mealEntity = MealEntity(
            id = meal.id,
            name = meal.name,
            restaurantId = meal.restaurantId,
            dateTime = meal.dateTime.toString(),
            photoList = meal.photoList,
            isSomeoneElsePaying = meal.isSomeoneElsePaying
        )
        val insertedMealId = mealDao.upsert(meal = mealEntity)

        // Use the actual meal ID (either the inserted ID for new meals, or existing ID for updates)
        val actualMealId = if (meal.id == 0) insertedMealId.toInt() else meal.id

        // Delete existing dishes for this meal if updating
        if (meal.id != 0) {
            dishDao.deleteByMealId(mealId = actualMealId)
        }

        // Save all dishes for this meal using the actual meal ID
        val dishEntityList = meal.dishList.map { dish ->
            dishMapper.mapDomainToEntity(dish = dish, mealId = actualMealId)
        }
        dishDao.upsertAll(dishList = dishEntityList)
    }

    override suspend fun deleteMeal(id: Int) {
        mealDao.deleteById(id = id)
    }
}