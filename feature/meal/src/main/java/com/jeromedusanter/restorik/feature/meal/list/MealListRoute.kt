package com.jeromedusanter.restorik.feature.meal.list

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme
import com.jeromedusanter.restorik.feature.meal.R
import com.jeromedusanter.restorik.feature.meal.navigation.FILTER_RESTAURANT_ID_KEY
import com.jeromedusanter.restorik.feature.meal.navigation.MEAL_DELETED_RESULT_KEY
import com.jeromedusanter.restorik.feature.meal.navigation.MEAL_SAVED_RESULT_KEY
import com.jeromedusanter.restorik.feature.meal.navigation.SHOW_FILTER_DIALOG_KEY
import com.jeromedusanter.restorik.feature.meal.navigation.navigateToMealDetail

@Composable
fun MealListRoute(
    modifier: Modifier = Modifier,
    viewModel: MealListViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    navController: NavHostController
) {
    val mealAddedSuccessMessage = stringResource(R.string.feature_meal_meal_added_successfully)
    val mealDeletedSuccessMessage = stringResource(R.string.feature_meal_meal_deleted_successfully)

    LaunchedEffect(Unit) {
        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getStateFlow(MEAL_SAVED_RESULT_KEY, false)?.collect { mealSaved ->
            if (mealSaved) {
                snackbarHostState.showSnackbar(
                    message = mealAddedSuccessMessage,
                    duration = SnackbarDuration.Short
                )
                savedStateHandle[MEAL_SAVED_RESULT_KEY] = false
            }
        }
    }

    LaunchedEffect(Unit) {
        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getStateFlow(MEAL_DELETED_RESULT_KEY, false)?.collect { mealDeleted ->
            if (mealDeleted) {
                snackbarHostState.showSnackbar(
                    message = mealDeletedSuccessMessage,
                    duration = SnackbarDuration.Short
                )
                savedStateHandle[MEAL_DELETED_RESULT_KEY] = false
            }
        }
    }

    LaunchedEffect(Unit) {
        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getStateFlow(SHOW_FILTER_DIALOG_KEY, false)?.collect { showDialog ->
            if (showDialog) {
                viewModel.showFilterDialog()
                savedStateHandle[SHOW_FILTER_DIALOG_KEY] = false
            }
        }
    }

    LaunchedEffect(Unit) {
        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getStateFlow(FILTER_RESTAURANT_ID_KEY, -1)?.collect { restaurantId ->
            if (restaurantId != -1) {
                viewModel.setRestaurantFilter(restaurantId = restaurantId)
                savedStateHandle[FILTER_RESTAURANT_ID_KEY] = -1
            }
        }
    }

    val uiState = viewModel.uiState.collectAsState()
    MealListScreen(
        uiState = uiState.value,
        modifier = modifier,
        onCloseRestaurantFilter = viewModel::clearRestaurantFilter,
        onClickMealItem = { mealId -> navController.navigateToMealDetail(mealId) },
        onApplyFilter = viewModel::applySortPreferences,
        onDismissFilterDialog = viewModel::hideFilterDialog

    )
}

@Preview(showBackground = true)
@Composable
private fun MealListRoutePreview() {
    RestorikTheme {
        MealListRoute(
            snackbarHostState = remember { SnackbarHostState() },
            navController = rememberNavController()
        )
    }
}