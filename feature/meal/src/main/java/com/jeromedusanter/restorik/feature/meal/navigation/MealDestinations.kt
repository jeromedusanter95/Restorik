package com.jeromedusanter.restorik.feature.meal.navigation

import androidx.navigation3.runtime.NavKey
import com.jeromedusanter.restorik.feature.meal.R
import kotlinx.serialization.Serializable

sealed interface MealDestinations : NavKey {
    val labelResId: Int
}

@Serializable
data object MealList : MealDestinations {
    override val labelResId = R.string.feature_meal_list_title
}

@Serializable
data class MealDetail(val mealId: Int) : MealDestinations {
    override val labelResId = R.string.feature_meal_detail_title
}

@Serializable
data class MealEditor(val mealId: Int = -1) : MealDestinations {
    override val labelResId = R.string.feature_meal_editor_title
}