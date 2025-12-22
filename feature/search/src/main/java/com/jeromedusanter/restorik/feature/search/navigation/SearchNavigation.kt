package com.jeromedusanter.restorik.feature.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jeromedusanter.restorik.feature.search.SearchScreen

const val SEARCH_ROUTE = "search"

fun NavController.navigateToSearch() {
    navigate(route = SEARCH_ROUTE)
}

fun NavGraphBuilder.searchScreen(
    onMealClick: (Int) -> Unit,
    onRestaurantClick: (Int) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onProvideSearchCallbacks: ((updateQuery: (String) -> Unit, clearQuery: () -> Unit, submitSearch: () -> Unit) -> Unit),
    onRequestSearchFocus: () -> Unit,
) {
    composable(route = SEARCH_ROUTE) {
        SearchScreen(
            onMealClick = onMealClick,
            onRestaurantClick = onRestaurantClick,
            onSearchQueryChanged = onSearchQueryChanged,
            onProvideSearchCallbacks = onProvideSearchCallbacks,
            onRequestSearchFocus = onRequestSearchFocus
        )
    }
}
