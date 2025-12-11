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
            comment = meal.comment,
            priceAsString = meal.price.toString(),
            ratingOnFive = meal.ratingOnFive,
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