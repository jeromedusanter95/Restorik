package com.jeromedusanter.restorik.core.testing.repository

import com.jeromedusanter.restorik.core.data.MealRepository
import com.jeromedusanter.restorik.core.model.Meal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.YearMonth

class TestMealRepository : MealRepository {

    /**
     * Internal storage for meals
     * MutableStateFlow allows us to emit new values and have all Flows react
     */
    private val mealsFlow = MutableStateFlow<List<Meal>>(emptyList())

    /**
     * Send test data to the repository
     * All Flows will automatically update based on this data
     */
    fun sendMeals(meals: List<Meal>) {
        mealsFlow.value = meals
    }

    override suspend fun getById(id: Int): Meal? {
        return mealsFlow.value.find { it.id == id }
    }

    override fun observeMealById(id: Int): Flow<Meal> {
        return mealsFlow.map { meals ->
            meals.find { it.id == id } ?: error("Meal with id $id not found")
        }
    }

    override fun observeAll(): Flow<List<Meal>> {
        return mealsFlow
    }

    override suspend fun saveMealInLocalDb(meal: Meal) {
        val currentMeals = mealsFlow.value.toMutableList()
        val existingIndex = currentMeals.indexOfFirst { it.id == meal.id }

        if (existingIndex != -1) {
            // Update existing meal
            currentMeals[existingIndex] = meal
        } else {
            // Add new meal
            currentMeals.add(meal)
        }

        mealsFlow.value = currentMeals
    }

    override suspend fun deleteMeal(id: Int) {
        mealsFlow.value = mealsFlow.value.filter { it.id != id }
    }

    override fun getTotalSpendingForMonth(yearMonth: String): Flow<Double> {
        return mealsFlow.map { meals ->
            meals.filter { meal ->
                val mealYM = YearMonth.of(meal.dateTime.year, meal.dateTime.month)
                mealYM == YearMonth.parse(yearMonth)
            }.sumOf { meal ->
                meal.dishList.sumOf { it.price }
            }
        }
    }

    override fun getMealCountForMonth(yearMonth: String): Flow<Int> {
        return mealsFlow.map { meals ->
            meals.count { meal ->
                val mealYM = YearMonth.of(meal.dateTime.year, meal.dateTime.month)
                mealYM == YearMonth.parse(yearMonth)
            }
        }
    }

    override fun getUniqueRestaurantCountForMonth(yearMonth: String): Flow<Int> {
        return mealsFlow.map { meals ->
            meals.filter { meal ->
                val mealYM = YearMonth.of(meal.dateTime.year, meal.dateTime.month)
                mealYM == YearMonth.parse(yearMonth)
            }.map { it.restaurantId }
                .distinct()
                .size
        }
    }

    override fun getAverageRatingForMonth(yearMonth: String): Flow<Double> {
        return mealsFlow.map { meals ->
            val mealsInMonth = meals.filter { meal ->
                val mealYM = YearMonth.of(meal.dateTime.year, meal.dateTime.month)
                mealYM == YearMonth.parse(yearMonth)
            }

            if (mealsInMonth.isEmpty()) {
                0.0
            } else {
                val totalRating = mealsInMonth.sumOf { meal ->
                    meal.dishList.map { it.rating.toDouble() }.average().takeIf { !it.isNaN() }
                        ?: 0.0
                }
                totalRating / mealsInMonth.size
            }
        }
    }

    override fun getFirstMealYearMonth(): Flow<YearMonth?> {
        return mealsFlow.map { meals ->
            meals.minByOrNull { it.dateTime }?.let { meal ->
                YearMonth.of(meal.dateTime.year, meal.dateTime.month)
            }
        }
    }

    override fun getTopRestaurantIdsBySpendingForMonth(
        yearMonth: String,
        limit: Int,
    ): Flow<List<Pair<Int, Double>>> {
        return mealsFlow.map { meals ->
            meals.filter { meal ->
                val mealYM = YearMonth.of(meal.dateTime.year, meal.dateTime.month)
                mealYM == YearMonth.parse(yearMonth)
            }.groupBy { it.restaurantId }
                .mapValues { (_, mealsForRestaurant) ->
                    mealsForRestaurant.sumOf { meal ->
                        meal.dishList.sumOf { it.price }
                    }
                }
                .toList()
                .sortedByDescending { (_, spending) -> spending }
                .take(n = limit)
        }
    }

    override fun getNewRestaurantsTriedCount(selectedYearMonth: String): Flow<Int> {
        return mealsFlow.map { meals ->
            val targetYearMonth = YearMonth.parse(selectedYearMonth)

            // Get restaurants visited this month
            val restaurantsThisMonth = meals.filter { meal ->
                val mealYM = YearMonth.of(meal.dateTime.year, meal.dateTime.month)
                mealYM == targetYearMonth
            }.map { it.restaurantId }.toSet()

            // Get restaurants visited before this month
            val restaurantsBeforeThisMonth = meals.filter { meal ->
                val mealYM = YearMonth.of(meal.dateTime.year, meal.dateTime.month)
                mealYM < targetYearMonth
            }.map { it.restaurantId }.toSet()

            // Count new restaurants (in this month but not before)
            restaurantsThisMonth.count { it !in restaurantsBeforeThisMonth }
        }
    }
}
