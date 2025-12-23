package com.jeromedusanter.restorik.feature.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileRoute(
    onNavigateToMonthSelector: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileScreen(
        uiState = uiState,
        onMonthChipClick = onNavigateToMonthSelector,
        onMonthChange = { yearMonth -> viewModel.selectMonth(yearMonth = yearMonth) },
        modifier = modifier
    )
}
