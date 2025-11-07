package com.jeromedusanter.restorik.core.model

data class MealGroupedByDate(
    val mealList: List<Meal>,
    val groupDate: GroupDate
)