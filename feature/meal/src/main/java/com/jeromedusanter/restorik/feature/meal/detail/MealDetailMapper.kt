package com.jeromedusanter.restorik.feature.meal.detail

import com.jeromedusanter.restorik.core.model.Meal
import javax.inject.Inject

class MealDetailMapper @Inject constructor() {

    fun mapToUiModel(meal: Meal): MealDetailUiState {
        return MealDetailUiState(meal.name)
    }
}