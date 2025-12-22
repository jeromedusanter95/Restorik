package com.jeromedusanter.restorik.feature.meal.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
fun MealListScreen(
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

    Column(modifier = Modifier.fillMaxSize()) {

        if (uiState.value.filterRestaurantName != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Start,
            ) {
                FilterChip(
                    selected = true,
                    onClick = { viewModel.clearRestaurantFilter() },
                    label = {
                        Text(text = uiState.value.filterRestaurantName ?: "")
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.feature_meal_clear_filter),
                            modifier = Modifier.size(size = 18.dp)
                        )
                    }
                )
            }
        }

        when {
            uiState.value.isLoading -> {
                MealListLoadingState()
            }

            uiState.value.groupedMealList.isEmpty() -> {
                MealListEmptyState()
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(all = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(space = 8.dp)
                ) {
                    uiState.value.groupedMealList.forEach { group ->
                        item(key = "header_${group.title}_${group.ratingValue}") {
                            if (group.ratingValue != null) {
                                // Display rating with star icon
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp, bottom = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${group.ratingValue}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            } else {
                                // Display text for date/restaurant groups
                                Text(
                                    text = group.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp, bottom = 4.dp)
                                )
                            }
                        }

                        items(
                            items = group.mealList,
                            key = { meal -> meal.id }
                        ) { meal ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInVertically(
                                    initialOffsetY = { it / 2 }
                                )
                            ) {
                                MealListItem(
                                    mealUiModel = meal,
                                    onClickItem = { navController.navigateToMealDetail(mealId = meal.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (uiState.value.showFilterDialog) {
        FilterSortDialog(
            currentSortMode = uiState.value.sortMode,
            currentSortOrder = uiState.value.sortOrder,
            onDismiss = viewModel::hideFilterDialog,
            onApply = viewModel::applySortPreferences
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MealListScreenPreview() {
    RestorikTheme {
        MealListScreen(
            snackbarHostState = remember { SnackbarHostState() },
            navController = rememberNavController()
        )
    }
}