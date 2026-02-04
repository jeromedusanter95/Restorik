package com.jeromedusanter.restorik.core.model

import java.time.LocalDateTime

data class Meal(
    val id: Int,
    val restaurantId: Int,
    val name: String,
    val dateTime: LocalDateTime,
    val photoList: List<String>,
    val dishList: List<Dish>
) {
    val ratingOnFive: Float
        get() = if (dishList.isEmpty()) 0f else dishList.map { it.rating }.average().toFloat()

    val price: Double
        get() = dishList.sumOf { it.price }
}