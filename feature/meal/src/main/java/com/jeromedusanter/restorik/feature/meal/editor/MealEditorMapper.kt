package com.jeromedusanter.restorik.feature.meal.editor

import android.net.Uri
import com.jeromedusanter.restorik.core.model.Meal
import com.jeromedusanter.restorik.core.model.Restaurant
import java.time.LocalDateTime
import javax.inject.Inject

class MealEditorMapper @Inject constructor() {

    fun toModel(
        mealEditorUiState: MealEditorUiState,
        restaurantId: Int
    ): Meal {
        return Meal(
            id = mealEditorUiState.id,
            restaurantId = restaurantId,
            name = mealEditorUiState.name,
            dateTime = LocalDateTime.now(),
            photoList = mealEditorUiState.photoUriList.map { it.toString() },
            dishList = mealEditorUiState.dishList
        )
    }

    fun toUiState(
        meal: Meal,
        restaurantName: String,
        maxPhotoCount: Int
    ): MealEditorUiState {
        val photoUriList = meal.photoList.map { Uri.parse(it) }
        val photoCount = photoUriList.size

        return MealEditorUiState(
            id = meal.id,
            restaurantName = restaurantName,
            name = meal.name,
            dishList = meal.dishList,
            photoUriList = photoUriList,
            isLoading = false,
            showAddButtonPhoto = photoUriList.isEmpty(),
            showAddButtonPhotoItem = photoCount < maxPhotoCount,
            photoTitleSuffix = "($photoCount/$maxPhotoCount)",
            showPhotoSelectionBottomSheet = false
        )
    }

    fun toRestaurantSuggestion(restaurant: Restaurant): RestaurantSuggestion {
        return RestaurantSuggestion(
            id = restaurant.id,
            name = restaurant.name
        )
    }
}