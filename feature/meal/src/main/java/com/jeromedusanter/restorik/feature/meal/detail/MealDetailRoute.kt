package com.jeromedusanter.restorik.feature.meal.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MealDetailRoute(
    modifier: Modifier = Modifier,
    viewModel: MealDetailViewModel = hiltViewModel(),
    onMealDeleted: () -> Unit = {}
) {
    val uiState = viewModel.uiState.collectAsState()

    MealDetailScreen(
        modifier = modifier,
        uiState = uiState.value,
        onDeleteClick = {
            viewModel.deleteMeal(onSuccess = onMealDeleted)
        }
    )
}