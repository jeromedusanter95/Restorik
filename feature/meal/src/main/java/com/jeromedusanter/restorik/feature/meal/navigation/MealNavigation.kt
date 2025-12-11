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

fun NavController.navigateToMealEditor(mealId: Int? = null, navOptions: NavOptions? = null) {
    val route = if (mealId != null) {
        "${MealDestinations.MealEditor.route}?${MealDestinations.MealEditor.mealIdArg}=$mealId"
    } else {
        MealDestinations.MealEditor.route
    }
    navigate(route = route, navOptions = navOptions)
}

const val MEAL_SAVED_RESULT_KEY = "meal_saved_result"
const val MEAL_EDITED_RESULT_KEY = "meal_edited_result"
const val MEAL_DELETED_RESULT_KEY = "meal_deleted_result"

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
            MealDetailRoute(
                onMealDeleted = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(MEAL_DELETED_RESULT_KEY, true)
                    navController.popBackStack()
                },
                navController = navController,
                snackbarHostState = snackbarHostState
            )
        }
        composable(
            route = MealDestinations.MealEditor.routeWithArgs,
            arguments = MealDestinations.MealEditor.arguments
        ) { backStackEntry ->
            val mealId = backStackEntry.arguments?.getInt(MealDestinations.MealEditor.mealIdArg, -1) ?: -1
            val isEditMode = mealId != -1

            MealEditorScreen(
                onMealSaved = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(if (isEditMode) MEAL_EDITED_RESULT_KEY else MEAL_SAVED_RESULT_KEY, true)
                    navController.popBackStack()
                },
                snackbarHostState = snackbarHostState
            )
        }
    }
}
