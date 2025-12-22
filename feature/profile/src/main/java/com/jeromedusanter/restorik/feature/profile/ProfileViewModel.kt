package com.jeromedusanter.restorik.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeromedusanter.restorik.core.data.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val mealRepository: MealRepository
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<ProfileUiState> = combine(
        _selectedMonth,
        mealRepository.observeAll()
    ) { selectedMonth, mealList ->
        val monthlyMeals = mealList.filter { meal ->
            YearMonth.from(meal.dateTime) == selectedMonth
        }
        val totalSpending = monthlyMeals.sumOf { it.price }

        ProfileUiState(
            selectedMonth = selectedMonth,
            monthlySpending = totalSpending,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = ProfileUiState.EMPTY
    )

    fun selectMonth(yearMonth: YearMonth) {
        _selectedMonth.value = yearMonth
    }
}
