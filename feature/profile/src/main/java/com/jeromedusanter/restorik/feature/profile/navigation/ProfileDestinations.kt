package com.jeromedusanter.restorik.feature.profile.navigation

import androidx.navigation3.runtime.NavKey
import com.jeromedusanter.restorik.feature.profile.R
import kotlinx.serialization.Serializable

sealed interface ProfileDestinations : NavKey {
    val labelResId: Int
}

@Serializable
data object Profile : ProfileDestinations {
    override val labelResId = R.string.feature_profile_title
}

@Serializable
data object MonthSelector : ProfileDestinations {
    override val labelResId = R.string.feature_profile_month_selector_title
}
