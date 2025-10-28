package com.jeromedusanter.restorik.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jeromedusanter.restorik.feature.meal.navigation.MealDestinations
import com.jeromedusanter.restorik.feature.meal.navigation.navigateToMealEditor
import com.jeromedusanter.restorik.navigation.RestorikNavHost

@Composable
fun RestorikApp(modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = modifier,
        topBar = {
            RestorikTopBar(
                modifier = modifier,
                title = stringResource(MealDestinations.getLabelByResId(currentRoute)),
                shouldShowBackButton = currentRoute != MealDestinations.MealList.route,
                onBackButtonClick = { navController.popBackStack() },
                onSearchButtonClick = { TODO() }
            )
        },
        bottomBar = {},
        snackbarHost = {},
        floatingActionButton = {
            if (currentRoute == MealDestinations.MealList.route) {
                FloatingActionButton(onClick = { navController.navigateToMealEditor() }) {
                    Icon(Icons.Filled.Add, "Add meal button")
                }
            }
        },
    ) { innerPadding ->
        RestorikNavHost(
            modifier = modifier.padding(innerPadding),
            navController = navController
        )
    }
}