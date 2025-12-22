package com.jeromedusanter.restorik.feature.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    onMealClick: (Int) -> Unit,
    onRestaurantClick: (Int) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onProvideSearchCallbacks: ((updateQuery: (String) -> Unit, clearQuery: () -> Unit, submitSearch: () -> Unit) -> Unit),
    onRequestSearchFocus: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    // Notify app-level of search query changes (for top bar)
    LaunchedEffect(uiState.query) {
        onSearchQueryChanged(uiState.query)
    }

    // Provide search control callbacks to parent
    LaunchedEffect(Unit) {
        onProvideSearchCallbacks(
            { newQuery -> viewModel.updateQuery(newQuery = newQuery) },
            { viewModel.clearQuery() },
            { viewModel.onSearchSubmitted(query = uiState.query) }
        )
        // Request focus on the search TextField when entering the screen
        onRequestSearchFocus()
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Content - top bar is handled by RestorikApp
        when {
            uiState.query.isEmpty() -> {
                if (uiState.recentSearchList.isEmpty()) {
                    EmptyRecentSearches()
                } else {
                    RecentSearches(
                        recentSearchList = uiState.recentSearchList,
                        onRecentSearchClick = { viewModel.onRecentSearchClicked(query = it) },
                        onDeleteClick = { viewModel.deleteRecentSearch(query = it) }
                    )
                }
            }

            uiState.query.length < 2 -> {
                MinimumCharactersMessage()
            }

            uiState.isSearching -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.searchResultList.isEmpty() -> {
                EmptySearchResults(query = uiState.query)
            }

            else -> {
                SearchResults(
                    searchResultList = uiState.searchResultList,
                    onMealClick = { mealId ->
                        // Save search to recent searches before navigating
                        viewModel.onSearchSubmitted(query = uiState.query)
                        onMealClick(mealId)
                    },
                    onRestaurantClick = { restaurantId ->
                        // Save search to recent searches before navigating
                        viewModel.onSearchSubmitted(query = uiState.query)
                        onRestaurantClick(restaurantId)
                    }
                )
            }
        }
    }
}
