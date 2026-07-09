package com.jeromedusanter.restorik.feature.profile.navigation

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.jeromedusanter.restorik.core.ui.navigation.Navigator
import com.jeromedusanter.restorik.core.ui.navigation.ResultEventBus
import com.jeromedusanter.restorik.feature.profile.MonthSelectorScreen
import com.jeromedusanter.restorik.feature.profile.ProfileRoute
import com.jeromedusanter.restorik.feature.profile.ProfileViewModel

fun EntryProviderScope<NavKey>.profileSection(
    navigator: Navigator,
    resultBus: ResultEventBus,
    viewModelStoreOwner: ViewModelStoreOwner
) {
    entry<Profile> {
        // Profile and MonthSelector share one ViewModel scoped to the activity,
        // so a month picked in MonthSelector is reflected back in Profile.
        val viewModel: ProfileViewModel = hiltViewModel(viewModelStoreOwner = viewModelStoreOwner)

        ProfileRoute(
            onNavigateToMonthSelector = { navigator.navigate(route = MonthSelector) },
            viewModel = viewModel
        )
    }

    entry<MonthSelector> {
        val viewModel: ProfileViewModel = hiltViewModel(viewModelStoreOwner = viewModelStoreOwner)
        val uiState = viewModel.uiState.collectAsState()

        MonthSelectorScreen(
            currentMonth = uiState.value.selectedMonth,
            minMonth = uiState.value.minMonth,
            onMonthSelected = { yearMonth ->
                viewModel.selectMonth(yearMonth = yearMonth)
                navigator.goBack()
            }
        )
    }
}
