package com.jeromedusanter.restorik.feature.meal.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme

@Composable
fun MealDetailRoute(
    modifier: Modifier = Modifier,
    viewModel: MealDetailViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()

    MealDetailScreen(
        modifier = modifier,
        uiState = uiState.value
    )
}