package com.jeromedusanter.restorik.feature.meal.editor

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.jeromedusanter.restorik.core.model.Dish
import com.jeromedusanter.restorik.core.model.DishType

@Immutable
data class MealEditorUiState(
    val id: Int,
    val restaurantName: String,
    val cityName: String,
    val name: String,
    val dishList: List<Dish>,
    val photoUriList: List<Uri>,
    val isLoading: Boolean,
    val showAddButtonPhoto: Boolean,
    val showAddButtonPhotoItem: Boolean,
    val photoTitleSuffix: String,
    val showPhotoSelectionBottomSheet: Boolean,
    val selectedPhotoUri: Uri? = null,
    val errorMessage: String? = null,
    val fieldErrors: FieldErrors = FieldErrors(),
    val restaurantSuggestionList: List<RestaurantSuggestionUiModel> = emptyList(),
    val citySuggestionList: List<CitySuggestionUiModel> = emptyList(),
    val showDishDialog: Boolean = false,
    val dishEditorState: DishEditorState = DishEditorState(),
    val isSomeoneElsePaying: Boolean = false
) {
    companion object {
        val EMPTY = MealEditorUiState(
            id = 0,
            restaurantName = "",
            cityName = "",
            name = "",
            dishList = emptyList(),
            photoUriList = emptyList(),
            isLoading = false,
            showAddButtonPhoto = true,
            showAddButtonPhotoItem = false,
            photoTitleSuffix = "",
            showPhotoSelectionBottomSheet = false,
            selectedPhotoUri = null,
            showDishDialog = false,
            dishEditorState = DishEditorState()
        )
    }
}

@Immutable
data class FieldErrors(
    val restaurantNameError: String? = null,
    val cityNameError: String? = null,
    val mealNameError: String? = null,
    val dishListError: String? = null
) {
    fun hasErrors(): Boolean =
        restaurantNameError != null || cityNameError != null || mealNameError != null || dishListError != null
}

enum class MealEditorField {
    RESTAURANT_NAME,
    CITY_NAME,
    MEAL_NAME,
    DISH_LIST
}

@Immutable
data class RestaurantSuggestionUiModel(
    val id: Int,
    val name: String
)

@Immutable
data class CitySuggestionUiModel(
    val id: Int,
    val name: String
)

@Immutable
data class DishEditorState(
    val dishId: Int = 0,
    val name: String = "",
    val description: String = "",
    val priceString: String = "",
    val rating: Float = 0f,
    val dishType: DishType = DishType.MAIN_COURSE,
    val isExpanded: Boolean = false,
    val nameError: String? = null,
    val priceError: String? = null,
    val ratingError: String? = null,
)