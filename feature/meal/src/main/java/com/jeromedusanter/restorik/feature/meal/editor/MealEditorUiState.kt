package com.jeromedusanter.restorik.feature.meal.editor

import android.net.Uri
import androidx.compose.runtime.Immutable

@Immutable
data class MealEditorUiState(
    val id: Int,
    val restaurantName: String,
    val name: String,
    val comment: String,
    val priceAsString: String,
    val ratingOnFive: Int,
    val photoUriList: List<Uri>,
    val isLoading: Boolean,
    val showAddButtonPhoto: Boolean,
    val showAddButtonPhotoItem: Boolean,
    val photoTitleSuffix: String,
    val showPhotoSelectionBottomSheet: Boolean,
    val selectedPhotoUri: Uri? = null,
    val errorMessage: String? = null,
    val fieldErrors: FieldErrors = FieldErrors(),
    val restaurantSuggestionList: List<RestaurantSuggestion> = emptyList()
) {
    companion object {
        val EMPTY = MealEditorUiState(
            id = 0,
            restaurantName = "",
            name = "",
            comment = "",
            priceAsString = "0.0",
            ratingOnFive = 0,
            photoUriList = emptyList(),
            isLoading = false,
            showAddButtonPhoto = true,
            showAddButtonPhotoItem = false,
            photoTitleSuffix = "",
            showPhotoSelectionBottomSheet = false,
            selectedPhotoUri = null
        )
    }
}

@Immutable
data class FieldErrors(
    val restaurantNameError: String? = null,
    val mealNameError: String? = null,
    val priceError: String? = null,
    val ratingError: String? = null
) {
    fun hasErrors(): Boolean = restaurantNameError != null || mealNameError != null || priceError != null || ratingError != null
}

enum class MealEditorField {
    RESTAURANT_NAME,
    MEAL_NAME,
    PRICE,
    RATING
}

@Immutable
data class RestaurantSuggestion(
    val id: Int,
    val name: String
)