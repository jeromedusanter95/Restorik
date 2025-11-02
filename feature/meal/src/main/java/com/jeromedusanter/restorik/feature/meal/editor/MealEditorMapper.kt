package com.jeromedusanter.restorik.feature.meal.editor

import com.jeromedusanter.restorik.core.model.Meal
import java.time.LocalDateTime
import javax.inject.Inject

class MealEditorMapper @Inject constructor() {

    fun toModel(
        mealEditorUiState: MealEditorUiState,
        restaurantId: Int
    ): Meal {
        val price = mealEditorUiState.priceAsString.toDoubleOrNull()
            ?: throw IllegalArgumentException("Invalid price format")

        return Meal(
            id = mealEditorUiState.id,
            restaurantId = restaurantId,
            name = mealEditorUiState.name,
            comment = mealEditorUiState.comment,
            price = price,
            dateTime = LocalDateTime.now(),
            ratingOnFive = mealEditorUiState.ratingOnFive,
            photoList = mealEditorUiState.photoUriList.map { it.toString() }
        )
    }
}