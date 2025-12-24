package com.jeromedusanter.restorik.feature.profile.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jeromedusanter.restorik.feature.profile.MonthSelectorScreen
import com.jeromedusanter.restorik.feature.profile.ProfileRoute
import com.jeromedusanter.restorik.feature.profile.ProfileViewModel

const val profileBaseRoute = "profile"

fun NavController.navigateToProfile(navOptions: NavOptions) =
    navigate(route = profileBaseRoute, navOptions)

fun NavController.navigateToMonthSelector() {
    navigate(route = ProfileDestinations.MonthSelector.route)
}

fun NavGraphBuilder.profileSection(navController: NavHostController) {
    navigation(
        startDestination = ProfileDestinations.Profile.route,
        route = profileBaseRoute
    ) {
        composable(route = ProfileDestinations.Profile.route) { backStackEntry ->
            // Get ViewModel from parent navigation entry to share state
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(profileBaseRoute)
            }
            val viewModel: ProfileViewModel = hiltViewModel(parentEntry)

            ProfileRoute(
                onNavigateToMonthSelector = { navController.navigateToMonthSelector() },
                viewModel = viewModel
            )
        }

        composable(route = ProfileDestinations.MonthSelector.route) { backStackEntry ->
            // Get ViewModel from parent navigation entry to share state
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(profileBaseRoute)
            }
            val viewModel: ProfileViewModel = hiltViewModel(parentEntry)

            MonthSelectorScreen(
                currentMonth = viewModel.uiState.value.selectedMonth,
                onMonthSelected = { yearMonth ->
                    viewModel.selectMonth(yearMonth = yearMonth)
                    navController.popBackStack()
                }
            )
        }
    }
}

// For compatibility - keep the simple version for direct use
fun NavGraphBuilder.profileScreen() {
    composable(route = ProfileDestinations.Profile.route) {
        ProfileRoute(
            onNavigateToMonthSelector = { }
        )
    }
}
