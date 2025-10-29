package com.jeromedusanter.restorik.feature.meal.editor

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MealEditorViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MealEditorUiState.EMPTY)
    val uiState: StateFlow<MealEditorUiState> = _uiState

    fun updateRestaurantName(restaurantName: String) {
        _uiState.value = _uiState.value.copy(restaurantName = restaurantName)
    }

    fun updateMealName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateMealComment(comment: String) {
        _uiState.value = _uiState.value.copy(comment = comment)
    }

    fun updatePrice(price: String) {
        _uiState.value = _uiState.value.copy(priceAsString = price)
    }

    fun updateRating(ratingOnFive: Int) {
        _uiState.value = _uiState.value.copy(ratingOnFive = ratingOnFive)
    }

    fun addPicture() {
        Log.d("MealEditorViewModel", "Saving meal: ${uiState.value}")
    }

    fun saveMeal() {
        Log.d("MealEditorViewModel", "Saving meal: ${uiState.value}")
    }
}
