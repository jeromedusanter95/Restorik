package com.jeromedusanter.restorik.feature.meal.editor

import android.content.Context
import android.database.sqlite.SQLiteException
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeromedusanter.restorik.core.data.MealRepository
import com.jeromedusanter.restorik.core.data.RestaurantRepository
import com.jeromedusanter.restorik.feature.meal.R
import com.jeromedusanter.restorik.feature.meal.navigation.MealDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MealEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mealRepository: MealRepository,
    private val restaurantRepository: RestaurantRepository,
    private val mealEditorMapper: MealEditorMapper,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {

    private val mealId: Int = savedStateHandle[MealDestinations.MealEditor.mealIdArg] ?: -1
    val isEditMode: Boolean = mealId != -1

    private val _uiState = MutableStateFlow(MealEditorUiState.EMPTY)
    val uiState: StateFlow<MealEditorUiState> = _uiState

    init {
        if (isEditMode) {
            loadMeal()
        }
    }

    private fun loadMeal() {
        viewModelScope.launch {
            try {
                val meal = mealRepository.observeMealById(id = mealId).first()
                val restaurant = restaurantRepository.observeById(id = meal.restaurantId).first()

                _uiState.value = mealEditorMapper.toUiState(
                    meal = meal,
                    restaurantName = restaurant.name,
                    maxPhotoCount = MAX_PHOTO_COUNT
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = context.getString(R.string.feature_meal_error_unknown)
                    )
                }
            }
        }
    }

    fun updateRestaurantName(restaurantName: String) {
        _uiState.value = _uiState.value.copy(restaurantName = restaurantName)
        searchRestaurants(query = restaurantName)
    }

    private fun searchRestaurants(query: String) {
        viewModelScope.launch {
            try {
                val suggestionList = if (query.length >= 2) {
                    restaurantRepository.searchByNamePrefix(query = query)
                        .map { mealEditorMapper.toRestaurantSuggestion(restaurant = it) }
                } else {
                    emptyList()
                }
                _uiState.update { it.copy(restaurantSuggestionList = suggestionList) }
            } catch (e: Exception) {
                _uiState.update { it.copy(restaurantSuggestionList = emptyList()) }
            }
        }
    }

    fun selectRestaurantSuggestion(suggestion: RestaurantSuggestion) {
        _uiState.update {
            it.copy(
                restaurantName = suggestion.name,
                restaurantSuggestionList = emptyList()
            )
        }
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

    fun addPhoto(uri: Uri) {
        _uiState.update { state ->
            val newList = state.photoUriList + uri
            val count = newList.size
            state.copy(
                photoUriList = newList,
                showAddButtonPhoto = newList.isEmpty(),
                showAddButtonPhotoItem = count < MAX_PHOTO_COUNT,
                photoTitleSuffix = "($count/$MAX_PHOTO_COUNT)"
            )
        }

    }

    fun deletePhoto(uri: Uri) {
        _uiState.update { state ->
            val newList = state.photoUriList - uri
            val count = newList.size
            state.copy(
                photoUriList = newList,
                showAddButtonPhoto = newList.isEmpty(),
                showAddButtonPhotoItem = count < MAX_PHOTO_COUNT,
                photoTitleSuffix = "($count/$MAX_PHOTO_COUNT)"
            )
        }
    }

    fun saveMeal(onSaveMealSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                // Clear any previous errors and set loading state
                _uiState.update { it.copy(isLoading = true, errorMessage = null, fieldErrors = FieldErrors()) }

                // Validate inputs and show field errors if any
                val validationErrors = validateInputs()
                if (validationErrors.hasErrors()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            fieldErrors = validationErrors,
                            errorMessage = context.getString(R.string.feature_meal_error_fix_above)
                        )
                    }
                    return@launch
                }

                // Perform database operations on IO dispatcher
                withContext(Dispatchers.IO) {
                    // Get or create restaurant
                    var restaurant = restaurantRepository.getRestaurantByName(uiState.value.restaurantName)
                    if (restaurant == null) {
                        restaurant = restaurantRepository.saveByNameAndGetLocal(uiState.value.restaurantName)
                    }

                    // Save meal (with existing ID if editing)
                    val meal = mealEditorMapper.toModel(
                        mealEditorUiState = uiState.value,
                        restaurantId = restaurant.id,
                    )
                    val finalMeal = if (isEditMode) {
                        meal.copy(id = mealId)
                    } else {
                        meal
                    }
                    mealRepository.saveMealInLocalDb(meal = finalMeal)
                }

                // Success - reset loading state
                _uiState.update { it.copy(isLoading = false) }
                onSaveMealSuccess()
            } catch (e: IllegalArgumentException) {
                // Handle validation errors from mapper or other sources
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: context.getString(R.string.feature_meal_error_invalid_input)
                    )
                }
            } catch (e: SQLiteException) {
                // Handle database-specific errors
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = context.getString(R.string.feature_meal_error_db_generic, getDatabaseErrorMessage(e))
                    )
                }
            } catch (e: IllegalStateException) {
                // Handle state-related errors (e.g., from RestaurantRepository)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: context.getString(R.string.feature_meal_error_invalid_state)
                    )
                }
            } catch (e: Exception) {
                // Handle all other unexpected errors
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = context.getString(R.string.feature_meal_error_save_meal_failed, e.message ?: context.getString(R.string.feature_meal_error_unknown))
                    )
                }
            }
        }
    }

    private fun validateInputs(): FieldErrors {
        val state = uiState.value

        val restaurantNameError = when {
            state.restaurantName.isBlank() -> context.getString(R.string.feature_meal_error_restaurant_name_required)
            else -> null
        }

        val mealNameError = when {
            state.name.isBlank() -> context.getString(R.string.feature_meal_error_meal_name_required)
            else -> null
        }

        val priceError = when {
            state.priceAsString.isBlank() -> context.getString(R.string.feature_meal_error_price_required)
            state.priceAsString.toDoubleOrNull() == null -> context.getString(R.string.feature_meal_error_invalid_price_format)
            state.priceAsString.toDouble() < 0 -> context.getString(R.string.feature_meal_error_price_negative)
            else -> null
        }

        val ratingError = when {
            state.ratingOnFive == 0 -> context.getString(R.string.feature_meal_error_rating_required)
            else -> null
        }

        return FieldErrors(
            restaurantNameError = restaurantNameError,
            mealNameError = mealNameError,
            priceError = priceError,
            ratingError = ratingError
        )
    }

    fun clearFieldError(field: MealEditorField) {
        _uiState.update {
            val newFieldErrors = when (field) {
                MealEditorField.RESTAURANT_NAME -> it.fieldErrors.copy(restaurantNameError = null)
                MealEditorField.MEAL_NAME -> it.fieldErrors.copy(mealNameError = null)
                MealEditorField.PRICE -> it.fieldErrors.copy(priceError = null)
                MealEditorField.RATING -> it.fieldErrors.copy(ratingError = null)
            }
            it.copy(fieldErrors = newFieldErrors)
        }
    }

    private fun getDatabaseErrorMessage(e: SQLiteException): String {
        return when {
            e.message?.contains("disk", ignoreCase = true) == true ->
                context.getString(R.string.feature_meal_error_db_no_space)
            e.message?.contains("corrupt", ignoreCase = true) == true ->
                context.getString(R.string.feature_meal_error_db_corrupted)
            e.message?.contains("lock", ignoreCase = true) == true ->
                context.getString(R.string.feature_meal_error_db_locked)
            else -> context.getString(R.string.feature_meal_error_db_unable_to_save)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun showPhotoSelectionBottomSheet() {
        _uiState.update { it.copy(showPhotoSelectionBottomSheet = true) }
    }

    fun hidePhotoSelectionBottomSheet() {
        _uiState.update { it.copy(showPhotoSelectionBottomSheet = false) }
    }

    companion object {
        const val MAX_PHOTO_COUNT = 5
    }
}
