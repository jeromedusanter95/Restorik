package com.jeromedusanter.restorik.core.domain

import com.jeromedusanter.restorik.core.data.MealRepository
import com.jeromedusanter.restorik.core.data.RestaurantRepository
import com.jeromedusanter.restorik.core.model.GroupDate
import com.jeromedusanter.restorik.core.model.Meal
import com.jeromedusanter.restorik.core.model.MealGroupedByDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class GetMealListUseCase @Inject constructor(
    private val mealRepository: MealRepository,
    private val restaurantRepository: RestaurantRepository,
) {

    operator fun invoke(): Flow<List<MealGroupedByDate>> {
        return mealRepository.observeAll().map { mealList ->
            mealList.groupByDateCategory()
        }
    }

    private fun List<Meal>.groupByDateCategory(): List<MealGroupedByDate> {
        val today = LocalDate.now()

        val grouped = groupBy { meal ->
            val mealDate = meal.dateTime.toLocalDate()
            when {
                mealDate == today -> GroupDate.TODAY
                mealDate == today.minusDays(1) -> GroupDate.YESTERDAY
                ChronoUnit.DAYS.between(mealDate, today) <= 7 -> GroupDate.WEEK
                ChronoUnit.DAYS.between(mealDate, today) <= 30 -> GroupDate.MONTH
                else -> GroupDate.OLDER
            }
        }

        val order = listOf(GroupDate.TODAY, GroupDate.YESTERDAY, GroupDate.WEEK, GroupDate.MONTH, GroupDate.OLDER)

        return order.mapNotNull { groupDate ->
            grouped[groupDate]?.let { mealList ->
                MealGroupedByDate(
                    mealList = mealList.sortedByDescending { it.dateTime },
                    groupDate = groupDate
                )
            }
        }
    }
}