package com.jeromedusanter.restorik.core.model

import java.time.LocalDateTime

data class Meal(
    val id: Int,
    val restaurantId: Int,
    val name: String,
    val comment: String,
    val price: Double,
    val dateTime: LocalDateTime,
    val ratingOnFive: Int,
    val photoList: List<String>
)