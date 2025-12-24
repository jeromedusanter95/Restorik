package com.jeromedusanter.restorik.feature.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onRecentSearchClick: (String) -> Unit,
    onDeleteRecentSearch: (String) -> Unit,
    onMealClick: (Int) -> Unit,
    onRestaurantClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
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
                        onRecentSearchClick = onRecentSearchClick,
                        onDeleteClick = onDeleteRecentSearch
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
                    onMealClick = onMealClick,
                    onRestaurantClick = onRestaurantClick
                )
            }
        }
    }
}
