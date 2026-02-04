package com.jeromedusanter.restorik.core.data

import com.jeromedusanter.restorik.core.database.model.DishEntity
import com.jeromedusanter.restorik.core.model.Dish
import com.jeromedusanter.restorik.core.model.DishType
import javax.inject.Inject

class DishMapper @Inject constructor() {

    fun mapEntityToDomain(dishEntity: DishEntity): Dish {
        return Dish(
            id = dishEntity.id,
            name = dishEntity.name,
            rating = dishEntity.rating,
            description = dishEntity.description,
            price = dishEntity.price,
            dishType = DishType.valueOf(dishEntity.dishType)
        )
    }

    fun mapDomainToEntity(dish: Dish, mealId: Int): DishEntity {
        return DishEntity(
            id = dish.id,
            mealId = mealId,
            name = dish.name,
            rating = dish.rating,
            description = dish.description,
            price = dish.price,
            dishType = dish.dishType.name
        )
    }
}
