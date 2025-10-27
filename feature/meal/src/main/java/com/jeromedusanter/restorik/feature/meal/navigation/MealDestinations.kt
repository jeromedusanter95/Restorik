package com.jeromedusanter.restorik.feature.meal.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class MealDestinations {
    abstract val route: String

    data object MealList : MealDestinations() {
        override val route = "meal_list"
    }

    data object MealDetail : MealDestinations() {
        override val route = "meal_details"
        const val mealIdArg = "meal_id"
        val routeWithArgs = "${route}/{${mealIdArg}}"
        val arguments = listOf(
            navArgument(mealIdArg) { type = NavType.IntType }
        )
    }

    data object MealEditor : MealDestinations() {
        override val route: String = "meal_editor"
    }
}