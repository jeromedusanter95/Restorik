package com.jeromedusanter.restorik.feature.search.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.jeromedusanter.restorik.feature.search.SearchRoute

fun EntryProviderScope<NavKey>.searchScreen(
    onMealClick: (Int) -> Unit,
    onRestaurantClick: (Int) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onProvideSearchCallbacks: ((updateQuery: (String) -> Unit, clearQuery: () -> Unit, submitSearch: () -> Unit) -> Unit),
    onRequestSearchFocus: () -> Unit,
) {
    entry<Search> {
        SearchRoute(
            onMealClick = onMealClick,
            onRestaurantClick = onRestaurantClick,
            onSearchQueryChanged = onSearchQueryChanged,
            onProvideSearchCallbacks = onProvideSearchCallbacks,
            onRequestSearchFocus = onRequestSearchFocus
        )
    }
}
