package com.jeromedusanter.restorik.feature.meal.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jeromedusanter.restorik.feature.meal.detail.MealDetailScreen
import com.jeromedusanter.restorik.feature.meal.editor.MealEditorScreen
import com.jeromedusanter.restorik.feature.meal.list.MealListScreen

const val mealBaseRoute = "meal"

fun NavController.navigateToMeal(navOptions: NavOptions) =
    navigate(route = mealBaseRoute, navOptions)

fun NavController.navigateToMealEditor(navOptions: NavOptions? = null) =
    navigate(route = MealDestinations.MealEditor.route, navOptions)

fun NavGraphBuilder.mealSection() {
    navigation(
        startDestination = MealDestinations.MealList.route,
        route = mealBaseRoute
    ) {
        composable(route = MealDestinations.MealList.route) {
            MealListScreen()
        }
        composable(
            route = MealDestinations.MealDetail.routeWithArgs,
            arguments = MealDestinations.MealDetail.arguments
        ) { navBackStackEntry ->
            MealDetailScreen(
                mealId = navBackStackEntry.arguments?.getInt(MealDestinations.MealDetail.mealIdArg),
            )
        }
        composable(route = MealDestinations.MealEditor.route) {
            MealEditorScreen()
        }
    }
}
