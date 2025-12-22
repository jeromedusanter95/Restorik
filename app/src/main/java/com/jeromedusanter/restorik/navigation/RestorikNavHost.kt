package com.jeromedusanter.restorik.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.jeromedusanter.restorik.feature.meal.navigation.FILTER_RESTAURANT_ID_KEY
import com.jeromedusanter.restorik.feature.meal.navigation.mealBaseRoute
import com.jeromedusanter.restorik.feature.meal.navigation.mealSection
import com.jeromedusanter.restorik.feature.meal.navigation.navigateToMealDetail
import com.jeromedusanter.restorik.feature.profile.navigation.profileSection
import com.jeromedusanter.restorik.feature.search.navigation.searchScreen

@Composable
fun RestorikNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    onSearchQueryChanged: (String) -> Unit,
    onProvideSearchCallbacks: ((updateQuery: (String) -> Unit, clearQuery: () -> Unit, submitSearch: () -> Unit) -> Unit),
    onRequestSearchFocus: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = mealBaseRoute,
        modifier = modifier,
    ) {
        mealSection(
            navController = navController,
            snackbarHostState = snackbarHostState
        )

        searchScreen(
            onMealClick = { mealId ->
                navController.popBackStack()
                navController.navigateToMealDetail(mealId = mealId)
            },
            onRestaurantClick = { restaurantId ->
                navController.popBackStack()
                navController.currentBackStackEntry?.savedStateHandle?.set(FILTER_RESTAURANT_ID_KEY, restaurantId)
            },
            onSearchQueryChanged = onSearchQueryChanged,
            onProvideSearchCallbacks = onProvideSearchCallbacks,
            onRequestSearchFocus = onRequestSearchFocus
        )

        profileSection(navController = navController)
    }
}