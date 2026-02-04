package com.jeromedusanter.restorik.core.data

import com.jeromedusanter.restorik.core.database.model.MealWithDishes
import com.jeromedusanter.restorik.core.model.Meal
import java.time.LocalDateTime
import javax.inject.Inject

class MealMapper @Inject constructor(
    private val dishMapper: DishMapper
) {

    fun mapEntityModelToDomainModel(mealWithDishes: MealWithDishes): Meal {
        return Meal(
            id = mealWithDishes.meal.id,
            restaurantId = mealWithDishes.meal.restaurantId,
            name = mealWithDishes.meal.name,
            dateTime = mealWithDishes.meal.dateTime.toLocalDateTime(),
            photoList = mealWithDishes.meal.photoList,
            dishList = mealWithDishes.dishList.map { dishMapper.mapEntityToDomain(dishEntity = it) }
        )
    }
}

private fun String.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.parse(this)
}
