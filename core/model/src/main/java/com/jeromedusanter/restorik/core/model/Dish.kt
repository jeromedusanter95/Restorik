package com.jeromedusanter.restorik.core.model

data class Dish(
    val id: Int,
    val name: String,
    val rating: Float,
    val description: String,
    val price: Double,
    val dishType: DishType
)
