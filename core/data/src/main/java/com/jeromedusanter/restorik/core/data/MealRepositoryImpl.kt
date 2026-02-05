package com.jeromedusanter.restorik.core.data

import com.jeromedusanter.restorik.core.database.dao.DishDao
import com.jeromedusanter.restorik.core.database.dao.MealDao
import com.jeromedusanter.restorik.core.database.model.MealEntity
import com.jeromedusanter.restorik.core.model.Meal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import java.time.LocalDateTime
import java.time.YearMonth
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

        val actualMealId = if (meal.id == 0) insertedMealId.toInt() else meal.id

        // When updating an existing meal, delete old dishes first
        if (meal.id != 0) {
            dishDao.deleteByMealId(mealId = actualMealId)
        }

        // IMPORTANT !!
        // I used negative ID for the dishes when saving in the UI
        // we need to reset all the negatives ID to 0 and let Room autogenerate an ID
        val dishEntityList = meal.dishList.map { dish ->
            val dishToSave = if (dish.id <= 0) {
                dish.copy(id = 0)
            } else {
                dish
            }
            dishMapper.mapDomainToEntity(dish = dishToSave, mealId = actualMealId)
        }
        dishDao.upsertAll(dishList = dishEntityList)
    }

    override suspend fun deleteMeal(id: Int) {
        mealDao.deleteById(id = id)
    }

    override fun getTotalSpendingForMonth(yearMonth: String): Flow<Double> {
        return mealDao.getTotalSpendingForMonth(yearMonth = yearMonth)
    }

    override fun getMealCountForMonth(yearMonth: String): Flow<Int> {
        return mealDao.getMealCountForMonth(yearMonth = yearMonth)
    }

    override fun getUniqueRestaurantCountForMonth(yearMonth: String): Flow<Int> {
        return mealDao.getUniqueRestaurantCountForMonth(yearMonth = yearMonth)
    }

    override fun getAverageRatingForMonth(yearMonth: String): Flow<Double> {
        return mealDao.getAverageRatingForMonth(yearMonth = yearMonth)
    }

    override fun getFirstMealYearMonth(): Flow<YearMonth?> {
        return mealDao.getFirstMealDate().map { dateStr ->
            dateStr?.let {
                val localDateTime = LocalDateTime.parse(it)
                YearMonth.from(localDateTime)
            }
        }
    }

    override fun getTopRestaurantIdsBySpendingForMonth(yearMonth: String, limit: Int): Flow<List<Pair<Int, Double>>> {
        return mealDao.getTopRestaurantsBySpendingForMonth(yearMonth = yearMonth, limit = limit).map { resultList ->
            resultList.map { result -> Pair(result.restaurantId, result.totalSpending) }
        }
    }

    override fun getNewRestaurantsTriedCount(selectedYearMonth: String): Flow<Int> = flow {
        val restaurantsThisMonth = mealDao.getUniqueRestaurantIdsForMonth(yearMonth = selectedYearMonth)
        val restaurantsBeforeThisMonth = mealDao.getUniqueRestaurantIdsBeforeMonth(yearMonth = selectedYearMonth)
        val count = restaurantsThisMonth.count { it !in restaurantsBeforeThisMonth }
        emit(count)
    }
}