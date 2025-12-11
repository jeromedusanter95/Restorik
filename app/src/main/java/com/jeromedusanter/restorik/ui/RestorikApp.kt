package com.jeromedusanter.restorik.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    val snackbarHostState = remember { SnackbarHostState() }

    // Check if we're in edit mode (meal editor with meal_id argument)
    val isEditMode = currentRoute?.startsWith(MealDestinations.MealEditor.route) == true &&
            (navBackStackEntry?.arguments?.getInt(MealDestinations.MealEditor.mealIdArg, -1) ?: -1) != -1

    val titleResId = when {
        currentRoute?.startsWith(MealDestinations.MealEditor.route) == true && isEditMode ->
            com.jeromedusanter.restorik.feature.meal.R.string.feature_meal_editor_edit_title
        else -> MealDestinations.getLabelByResId(currentRoute)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            RestorikTopBar(
                modifier = modifier,
                title = stringResource(titleResId),
                shouldShowBackButton = currentRoute != MealDestinations.MealList.route,
                onBackButtonClick = { navController.popBackStack() },
                onSearchButtonClick = { TODO() },
                actions = {
                    // Show edit icon only on meal detail screen
                    if (currentRoute == MealDestinations.MealDetail.routeWithArgs) {
                        val mealId = navBackStackEntry?.arguments?.getInt(MealDestinations.MealDetail.mealIdArg)
                        if (mealId != null) {
                            IconButton(onClick = { navController.navigateToMealEditor(mealId = mealId) }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = stringResource(com.jeromedusanter.restorik.feature.meal.R.string.feature_meal_edit_content_description)
                                )
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {},
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
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
            navController = navController,
            snackbarHostState = snackbarHostState
        )
    }
}