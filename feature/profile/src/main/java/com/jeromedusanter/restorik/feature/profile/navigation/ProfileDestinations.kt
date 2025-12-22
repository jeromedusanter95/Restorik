package com.jeromedusanter.restorik.feature.profile.navigation

import com.jeromedusanter.restorik.feature.profile.R

sealed class ProfileDestinations {
    abstract val route: String
    abstract val labelResId: Int

    data object Profile : ProfileDestinations() {
        override val route = "profile"
        override val labelResId = R.string.feature_profile_title
    }
}
