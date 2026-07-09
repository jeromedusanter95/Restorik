package com.jeromedusanter.restorik.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.jeromedusanter.restorik.core.ui.navigation.LocalResultEventBus
import com.jeromedusanter.restorik.core.ui.navigation.NavigationState
import com.jeromedusanter.restorik.core.ui.navigation.Navigator
import com.jeromedusanter.restorik.core.ui.navigation.ResultEventBus
import com.jeromedusanter.restorik.core.ui.navigation.toEntries
import com.jeromedusanter.restorik.feature.meal.navigation.MealDetail
import com.jeromedusanter.restorik.feature.meal.navigation.mealSection
import com.jeromedusanter.restorik.feature.profile.navigation.profileSection
import com.jeromedusanter.restorik.feature.search.navigation.searchScreen

@Composable
fun RestorikNavHost(
    modifier: Modifier = Modifier,
    navigationState: NavigationState,
    navigator: Navigator,
    resultBus: ResultEventBus,
    snackbarHostState: SnackbarHostState,
    onSearchQueryChanged: (String) -> Unit,
    onProvideSearchCallbacks: ((updateQuery: (String) -> Unit, clearQuery: () -> Unit, submitSearch: () -> Unit) -> Unit),
    onRequestSearchFocus: () -> Unit,
) {
    // Captured here (above NavDisplay) so it resolves to the activity, not a
    // per-entry owner. Profile and MonthSelector share this owner's ViewModel.
    val topLevelViewModelStoreOwner = LocalViewModelStoreOwner.current
        ?: error("No ViewModelStoreOwner found")

    val entryProvider = entryProvider {
        mealSection(
            navigator = navigator,
            resultBus = resultBus,
            snackbarHostState = snackbarHostState
        )

        searchScreen(
            onMealClick = { mealId ->
                navigator.goBack()
                navigator.navigate(route = MealDetail(mealId = mealId))
            },
            onRestaurantClick = { restaurantId ->
                navigator.goBack()
                resultBus.sendResult(resultKey = "filter_restaurant_id", result = restaurantId)
            },
            onSearchQueryChanged = onSearchQueryChanged,
            onProvideSearchCallbacks = onProvideSearchCallbacks,
            onRequestSearchFocus = onRequestSearchFocus
        )

        profileSection(
            navigator = navigator,
            resultBus = resultBus,
            viewModelStoreOwner = topLevelViewModelStoreOwner
        )
    }

    CompositionLocalProvider(LocalResultEventBus provides resultBus) {
        NavDisplay(
            entries = navigationState.toEntries(entryProvider = entryProvider),
            onBack = { navigator.goBack() },
            modifier = modifier
        )
    }
}