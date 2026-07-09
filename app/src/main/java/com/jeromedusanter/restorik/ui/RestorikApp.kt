package com.jeromedusanter.restorik.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Save
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
import com.jeromedusanter.restorik.feature.meal.navigation.MealDestinations
import com.jeromedusanter.restorik.feature.meal.navigation.MealDetail
import com.jeromedusanter.restorik.feature.meal.navigation.MealEditor
import com.jeromedusanter.restorik.feature.meal.navigation.MealList
import com.jeromedusanter.restorik.feature.profile.navigation.Profile
import com.jeromedusanter.restorik.feature.profile.navigation.ProfileDestinations
import com.jeromedusanter.restorik.feature.search.navigation.Search
import com.jeromedusanter.restorik.core.ui.navigation.Navigator
import com.jeromedusanter.restorik.core.ui.navigation.ResultEventBus
import com.jeromedusanter.restorik.core.ui.navigation.rememberNavigationState
import com.jeromedusanter.restorik.navigation.RestorikNavHost

@Composable
fun RestorikApp(modifier: Modifier = Modifier) {

    val navigationState = rememberNavigationState(
        startRoute = MealList,
        topLevelRoutes = setOf(MealList, Profile, Search)
    )
    val navigator = remember { Navigator(state = navigationState) }
    val resultBus = remember { ResultEventBus() }

    val snackbarHostState = remember { SnackbarHostState() }

    // Get current route from navigation state
    val currentRoute = navigationState.backStacks[navigationState.topLevelRoute]?.last()

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
    val isSearchMode = currentRoute is Search

    // Check if we're in edit mode (meal editor with meal_id argument)
    val isEditMode = currentRoute is MealEditor && currentRoute.mealId != -1

    val titleResId = when (currentRoute) {
        is MealEditor -> if (isEditMode) {
            com.jeromedusanter.restorik.feature.meal.R.string.feature_meal_editor_edit_title
        } else {
            com.jeromedusanter.restorik.feature.meal.R.string.feature_meal_editor_title
        }
        is Profile -> com.jeromedusanter.restorik.feature.profile.R.string.feature_profile_title
        is ProfileDestinations -> currentRoute.labelResId
        is MealDestinations -> currentRoute.labelResId
        else -> com.jeromedusanter.restorik.feature.meal.R.string.feature_meal_list_title
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            RestorikTopBar(
                modifier = modifier,
                title = stringResource(id = titleResId),
                shouldShowBackButton = currentRoute !is MealList && currentRoute !is Profile && !isSearchMode,
                onBackButtonClick = { navigator.goBack() },
                onSearchButtonClick = { navigator.navigate(route = Search) },
                isSearchMode = isSearchMode,
                searchQuery = searchQuery.value,
                onSearchQueryChange = { newValue ->
                    searchQuery.value = newValue
                    searchCallbacks.value?.invoke(newValue.text)
                },
                onSearchSubmit = { submitSearchCallback.value?.invoke() },
                onClearSearch = {
                    searchQuery.value = TextFieldValue(text = "")
                    clearSearchCallback.value?.invoke()
                },
                searchFocusRequester = searchFocusRequester,
                actions = {
                    // Show filter icon on meal list screen
                    if (currentRoute is MealList) {
                        IconButton(onClick = {
                            // Trigger filter dialog via result event bus
                            resultBus.sendResult(resultKey = "show_filter_dialog", result = true)
                        }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = stringResource(id = com.jeromedusanter.restorik.feature.meal.R.string.feature_meal_filter_icon_content_description)
                            )
                        }
                    }
                    // Show edit icon only on meal detail screen
                    if (currentRoute is MealDetail) {
                        val mealId = currentRoute.mealId
                        IconButton(onClick = { navigator.navigate(route = MealEditor(mealId = mealId)) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(id = com.jeromedusanter.restorik.feature.meal.R.string.feature_meal_edit_content_description)
                            )
                        }
                    }
                    // Show save icon on meal editor screen
                    if (currentRoute is MealEditor) {
                        IconButton(onClick = {
                            // Trigger save via result event bus
                            resultBus.sendResult(resultKey = "trigger_save_meal", result = true)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = stringResource(id = com.jeromedusanter.restorik.feature.meal.R.string.feature_meal_save_button)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (currentRoute is MealList || currentRoute is Profile) {
                RestorikBottomBar(
                    topLevelRoute = navigationState.topLevelRoute,
                    onMealClick = {
                        navigator.navigate(route = MealList)
                    },
                    onProfileClick = {
                        navigator.navigate(route = Profile)
                    }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            if (currentRoute is MealList) {
                FloatingActionButton(onClick = { navigator.navigate(route = MealEditor()) }) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add meal button")
                }
            }
        },
    ) { innerPadding ->
        RestorikNavHost(
            modifier = modifier.padding(paddingValues = innerPadding),
            navigationState = navigationState,
            navigator = navigator,
            resultBus = resultBus,
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