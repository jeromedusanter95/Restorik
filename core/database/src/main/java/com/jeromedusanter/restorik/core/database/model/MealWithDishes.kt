package com.jeromedusanter.restorik.core.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class MealWithDishes(
    @Embedded val meal: MealEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "meal_id"
    )
    val dishList: List<DishEntity>
)
