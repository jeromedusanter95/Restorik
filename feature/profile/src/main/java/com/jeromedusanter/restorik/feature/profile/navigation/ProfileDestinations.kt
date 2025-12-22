package com.jeromedusanter.restorik.feature.profile.navigation

import com.jeromedusanter.restorik.feature.profile.R

sealed class ProfileDestinations {
    abstract val route: String
    abstract val labelResId: Int

    data object Profile : ProfileDestinations() {
        override val route = "profile_screen"
        override val labelResId = R.string.feature_profile_title
    }

    data object MonthSelector : ProfileDestinations() {
        override val route = "month_selector"
        override val labelResId = R.string.feature_profile_month_selector_title
    }
}
