package com.jeromedusanter.restorik.feature.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SearchRoute(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    onMealClick: (Int) -> Unit,
    onRestaurantClick: (Int) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onProvideSearchCallbacks: ((updateQuery: (String) -> Unit, clearQuery: () -> Unit, submitSearch: () -> Unit) -> Unit),
    onRequestSearchFocus: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.query) {
        onSearchQueryChanged(uiState.query)
    }

    LaunchedEffect(Unit) {
        onProvideSearchCallbacks(
            { newQuery -> viewModel.updateQuery(newQuery = newQuery) },
            { viewModel.clearQuery() },
            { viewModel.onSearchSubmitted(query = uiState.query) }
        )
        onRequestSearchFocus()
    }

    SearchScreen(
        uiState = uiState,
        onRecentSearchClick = { viewModel.onRecentSearchClicked(query = it) },
        onDeleteRecentSearch = { viewModel.deleteRecentSearch(query = it) },
        onMealClick = { mealId ->
            viewModel.onSearchSubmitted(query = uiState.query)
            onMealClick(mealId)
        },
        onRestaurantClick = { restaurantId ->
            viewModel.onSearchSubmitted(query = uiState.query)
            onRestaurantClick(restaurantId)
        },
        modifier = modifier
    )
}
