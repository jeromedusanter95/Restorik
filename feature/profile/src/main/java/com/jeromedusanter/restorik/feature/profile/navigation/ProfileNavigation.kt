package com.jeromedusanter.restorik.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.jeromedusanter.restorik.feature.profile.ProfileScreen

fun NavController.navigateToProfile(navOptions: NavOptions) =
    navigate(route = ProfileDestinations.Profile.route, navOptions)

fun NavGraphBuilder.profileScreen() {
    composable(route = ProfileDestinations.Profile.route) {
        ProfileScreen()
    }
}
