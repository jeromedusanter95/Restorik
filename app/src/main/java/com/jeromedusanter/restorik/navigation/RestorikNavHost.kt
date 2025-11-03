package com.jeromedusanter.restorik.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.jeromedusanter.restorik.feature.meal.navigation.mealBaseRoute
import com.jeromedusanter.restorik.feature.meal.navigation.mealSection

@Composable
fun RestorikNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState
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
    }
}