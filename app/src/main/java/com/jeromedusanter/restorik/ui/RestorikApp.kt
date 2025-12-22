package com.jeromedusanter.restorik.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jeromedusanter.restorik.feature.meal.navigation.MealDestinations
import com.jeromedusanter.restorik.feature.meal.navigation.navigateToMealEditor
import com.jeromedusanter.restorik.feature.search.navigation.SEARCH_ROUTE
import com.jeromedusanter.restorik.feature.search.navigation.navigateToSearch
import com.jeromedusanter.restorik.navigation.RestorikNavHost

@Composable
fun RestorikApp(modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val snackbarHostState = remember { SnackbarHostState() }

    // Track search query and callbacks for the top bar
    val searchQuery = remember { mutableStateOf(TextFieldValue("")) }
    val searchCallbacks = remember { mutableStateOf<((String) -> Unit)?>(null) }
    val clearSearchCallback = remember { mutableStateOf<(() -> Unit)?>(null) }
    val submitSearchCallback = remember { mutableStateOf<(() -> Unit)?>(null) }
    val searchFocusRequester = remember { FocusRequester() }
    val shouldRequestFocus = remember { mutableStateOf(false) }

    // Request focus when shouldRequestFocus is true
    LaunchedEffect(shouldRequestFocus.value) {
        if (shouldRequestFocus.value) {
            searchFocusRequester.requestFocus()
            shouldRequestFocus.value = false
        }
    }

    // Check if we're on search screen
    val isSearchMode = currentRoute == SEARCH_ROUTE

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
                shouldShowBackButton = currentRoute != MealDestinations.MealList.route && !isSearchMode,
                onBackButtonClick = { navController.popBackStack() },
                onSearchButtonClick = { navController.navigateToSearch() },
                isSearchMode = isSearchMode,
                searchQuery = searchQuery.value,
                onSearchQueryChange = { newValue ->
                    searchQuery.value = newValue
                    searchCallbacks.value?.invoke(newValue.text)
                },
                onSearchSubmit = { submitSearchCallback.value?.invoke() },
                onClearSearch = {
                    searchQuery.value = TextFieldValue("")
                    clearSearchCallback.value?.invoke()
                },
                searchFocusRequester = searchFocusRequester,
                actions = {
                    // Show filter icon on meal list screen
                    if (currentRoute == MealDestinations.MealList.route) {
                        IconButton(onClick = {
                            // Trigger filter dialog via saved state handle
                            navController.currentBackStackEntry?.savedStateHandle?.set("show_filter_dialog", true)
                        }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = stringResource(com.jeromedusanter.restorik.feature.meal.R.string.feature_meal_filter_icon_content_description)
                            )
                        }
                    }
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
            snackbarHostState = snackbarHostState,
            onSearchQueryChanged = { newText ->
                // Only update if the text is different to avoid cursor jumping
                if (searchQuery.value.text != newText) {
                    searchQuery.value = searchQuery.value.copy(text = newText)
                }
            },
            onProvideSearchCallbacks = { updateQuery, clearQuery, submitSearch ->
                searchCallbacks.value = updateQuery
                clearSearchCallback.value = clearQuery
                submitSearchCallback.value = submitSearch
            },
            onRequestSearchFocus = { shouldRequestFocus.value = true }
        )
    }
}