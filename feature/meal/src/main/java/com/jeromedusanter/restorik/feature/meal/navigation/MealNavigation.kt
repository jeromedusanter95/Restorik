package com.jeromedusanter.restorik.feature.meal.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jeromedusanter.restorik.feature.meal.detail.MealDetailRoute
import com.jeromedusanter.restorik.feature.meal.editor.MealEditorScreen
import com.jeromedusanter.restorik.feature.meal.list.MealListScreen

const val mealBaseRoute = "meal"

fun NavController.navigateToMeal(navOptions: NavOptions) =
    navigate(route = mealBaseRoute, navOptions)

fun NavController.navigateToMealDetail(mealId: Int) {
    navigate(route = "${MealDestinations.MealDetail.route}/$mealId")
}

fun NavController.navigateToMealEditor(navOptions: NavOptions? = null) =
    navigate(route = MealDestinations.MealEditor.route, navOptions)

const val MEAL_SAVED_RESULT_KEY = "meal_saved_result"

fun NavGraphBuilder.mealSection(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState
) {
    navigation(
        startDestination = MealDestinations.MealList.route,
        route = mealBaseRoute
    ) {
        composable(route = MealDestinations.MealList.route) {
            MealListScreen(
                snackbarHostState = snackbarHostState,
                navController = navController
            )
        }
        composable(
            route = MealDestinations.MealDetail.routeWithArgs,
            arguments = MealDestinations.MealDetail.arguments
        ) {
            MealDetailRoute()
        }
        composable(route = MealDestinations.MealEditor.route) {
            MealEditorScreen(
                onMealSaved = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(MEAL_SAVED_RESULT_KEY, true)
                    navController.popBackStack()
                },
                snackbarHostState = snackbarHostState
            )
        }
    }
}
