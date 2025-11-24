package com.jeromedusanter.restorik.core.data

import com.jeromedusanter.restorik.core.database.model.MealEntity
import com.jeromedusanter.restorik.core.model.Meal
import java.time.LocalDateTime
import javax.inject.Inject

class MealMapper @Inject constructor() {

    fun mapEntityModelToDomainModel(mealEntity: MealEntity): Meal {
        return Meal(
            id = mealEntity.id,
            restaurantId = mealEntity.restaurantId,
            name = mealEntity.name,
            comment = mealEntity.comment,
            price = mealEntity.price,
            dateTime = mealEntity.dateTime.toLocalDateTime(),
            ratingOnFive = mealEntity.ratingOnFive,
            photoList = mealEntity.photoList
        )
    }
}

private fun String.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.parse(this)
}
