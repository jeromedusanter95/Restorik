package com.jeromedusanter.restorik.feature.meal.list

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme
import com.jeromedusanter.restorik.core.ui.navigation.Navigator
import com.jeromedusanter.restorik.core.ui.navigation.ResultEffect
import com.jeromedusanter.restorik.core.ui.navigation.ResultEventBus
import com.jeromedusanter.restorik.feature.meal.R
import com.jeromedusanter.restorik.feature.meal.navigation.MealDetail
import androidx.compose.runtime.collectAsState

@Composable
fun MealListRoute(
    modifier: Modifier = Modifier,
    viewModel: MealListViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    navigator: Navigator,
    resultBus: ResultEventBus
) {
    val mealAddedSuccessMessage = stringResource(id = R.string.feature_meal_meal_added_successfully)
    val mealDeletedSuccessMessage = stringResource(id = R.string.feature_meal_meal_deleted_successfully)

    // Handle meal saved result
    ResultEffect<Boolean>(resultEventBus = resultBus, resultKey = "meal_saved_result") { mealSaved ->
        if (mealSaved) {
            snackbarHostState.showSnackbar(
                message = mealAddedSuccessMessage,
                duration = SnackbarDuration.Short
            )
        }
    }

    // Handle meal deleted result
    ResultEffect<Boolean>(resultEventBus = resultBus, resultKey = "meal_deleted_result") { mealDeleted ->
        if (mealDeleted) {
            snackbarHostState.showSnackbar(
                message = mealDeletedSuccessMessage,
                duration = SnackbarDuration.Short
            )
        }
    }

    // Handle show filter dialog trigger
    ResultEffect<Boolean>(resultEventBus = resultBus, resultKey = "show_filter_dialog") { showDialog ->
        if (showDialog) {
            viewModel.showFilterDialog()
        }
    }

    // Handle restaurant filter from search
    ResultEffect<Int>(resultEventBus = resultBus, resultKey = "filter_restaurant_id") { restaurantId ->
        if (restaurantId != -1) {
            viewModel.setRestaurantFilter(restaurantId = restaurantId)
        }
    }

    val uiState = viewModel.uiState.collectAsState()
    MealListScreen(
        uiState = uiState.value,
        modifier = modifier,
        onCloseRestaurantFilter = viewModel::clearRestaurantFilter,
        onClickMealItem = { mealId -> navigator.navigate(route = MealDetail(mealId = mealId)) },
        onApplyFilter = viewModel::applySortPreferences,
        onDismissFilterDialog = viewModel::hideFilterDialog
    )
}

@Preview(showBackground = true)
@Composable
private fun MealListRoutePreview() {
    RestorikTheme {
        MealListScreen(
            uiState = MealListUiState(
                groupedMealList = emptyList(),
                isLoading = false,
                sortMode = com.jeromedusanter.restorik.core.model.SortMode.DATE,
                sortOrder = com.jeromedusanter.restorik.core.model.SortOrder.ASCENDING,
                showFilterDialog = false,
                filterRestaurantName = null
            ),
            onCloseRestaurantFilter = {},
            onClickMealItem = {},
            onApplyFilter = { _, _ -> },
            onDismissFilterDialog = {}
        )
    }
}