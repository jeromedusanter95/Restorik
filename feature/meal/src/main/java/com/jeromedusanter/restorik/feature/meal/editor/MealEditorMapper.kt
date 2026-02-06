package com.jeromedusanter.restorik.feature.meal.editor

import androidx.core.net.toUri
import com.jeromedusanter.restorik.core.model.City
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
            dishList = mealEditorUiState.dishList,
            isSomeoneElsePaying = mealEditorUiState.isSomeoneElsePaying
        )
    }

    fun toUiState(
        meal: Meal,
        restaurantName: String,
        cityName: String,
        maxPhotoCount: Int
    ): MealEditorUiState {
        val photoUriList = meal.photoList.map { it.toUri() }
        val photoCount = photoUriList.size

        return MealEditorUiState(
            id = meal.id,
            restaurantName = restaurantName,
            cityName = cityName,
            name = meal.name,
            dishList = meal.dishList,
            photoUriList = photoUriList,
            isLoading = false,
            showAddButtonPhoto = photoUriList.isEmpty(),
            showAddButtonPhotoItem = photoCount < maxPhotoCount,
            photoTitleSuffix = "($photoCount/$maxPhotoCount)",
            showPhotoSelectionBottomSheet = false,
            isSomeoneElsePaying = meal.isSomeoneElsePaying
        )
    }

    fun toRestaurantSuggestion(restaurant: Restaurant): RestaurantSuggestionUiModel {
        return RestaurantSuggestionUiModel(
            id = restaurant.id,
            name = restaurant.name
        )
    }

    fun toCitySuggestion(city: City): CitySuggestionUiModel {
        return CitySuggestionUiModel(
            id = city.id,
            name = city.name
        )
    }
}