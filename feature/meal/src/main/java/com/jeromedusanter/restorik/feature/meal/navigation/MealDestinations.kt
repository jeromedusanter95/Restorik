package com.jeromedusanter.restorik.feature.meal.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.jeromedusanter.restorik.feature.meal.R

sealed class MealDestinations {
    abstract val route: String
    abstract val labelResId: Int

    data object MealList : MealDestinations() {
        override val route = "meal_list"
        override val labelResId = R.string.feature_meal_list_title
    }

    data object MealDetail : MealDestinations() {
        override val route = "meal_details"
        const val mealIdArg = "meal_id"
        val routeWithArgs = "${route}/{${mealIdArg}}"
        val arguments = listOf(
            navArgument(mealIdArg) { type = NavType.IntType }
        )
        override val labelResId = R.string.feature_meal_detail_title
    }

    data object MealEditor : MealDestinations() {
        override val route: String = "meal_editor"
        override val labelResId = R.string.feature_meal_editor_title
    }

    companion object {
        fun getLabelByResId(route: String?): Int {
            return when (route) {
                MealList.route -> MealList.labelResId
                MealDetail.routeWithArgs -> MealDetail.labelResId
                MealEditor.route -> MealEditor.labelResId
                else -> MealList.labelResId
            }
        }
    }
}