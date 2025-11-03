package com.jeromedusanter.restorik.feature.meal.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeromedusanter.restorik.core.data.MealRepository
import com.jeromedusanter.restorik.core.model.Meal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MealListViewModel @Inject constructor(
    private val mealRepository: MealRepository
) : ViewModel() {

    val uiState: StateFlow<MealListUiState> = mealRepository.observeAll()
        .map { mealList ->
            MealListUiState(
                mealList = mealList.map { meal -> meal.toUiModel() },
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = MealListUiState.EMPTY
        )

    private fun Meal.toUiModel(): MealUiModel {
        return MealUiModel(
            id = id,
            name = name
        )
    }
}