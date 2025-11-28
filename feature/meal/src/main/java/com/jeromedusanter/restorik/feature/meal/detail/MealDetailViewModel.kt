package com.jeromedusanter.restorik.feature.meal.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeromedusanter.restorik.core.data.MealRepository
import com.jeromedusanter.restorik.feature.meal.navigation.MealDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MealDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    mealRepository: MealRepository,
    mapper: MealDetailMapper
) : ViewModel() {

    private val mealId: Int = checkNotNull(savedStateHandle[MealDestinations.MealDetail.mealIdArg])

    val uiState: StateFlow<MealDetailUiState> = mealRepository.observeMealById(mealId)
        .map { meal -> mapper.mapToUiModel(meal) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = MealDetailUiState.EMPTY
        )
}