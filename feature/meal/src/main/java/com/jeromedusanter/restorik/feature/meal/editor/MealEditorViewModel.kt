package com.jeromedusanter.restorik.feature.meal.editor

import android.database.sqlite.SQLiteException
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeromedusanter.restorik.core.common.resources.ResourceProvider
import com.jeromedusanter.restorik.core.data.CityRepository
import com.jeromedusanter.restorik.core.data.MealRepository
import com.jeromedusanter.restorik.core.data.RestaurantRepository
import com.jeromedusanter.restorik.core.model.Dish
import com.jeromedusanter.restorik.core.model.DishType
import com.jeromedusanter.restorik.feature.meal.R
import com.jeromedusanter.restorik.feature.meal.navigation.MealDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val cityRepository: CityRepository,
    private val mealEditorMapper: MealEditorMapper,
    private val resourceProvider: ResourceProvider,
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
                val meal = mealRepository.getById(id = mealId)
                if (meal == null) {
                    _uiState.update {
                        it.copy(errorMessage = "Meal not found")
                    }
                    return@launch
                }

                val restaurant = restaurantRepository.getById(id = meal.restaurantId)
                if (restaurant == null) {
                    _uiState.update {
                        it.copy(errorMessage = "Restaurant not found")
                    }
                    return@launch
                }

                val city = cityRepository.getById(id = restaurant.cityId)
                if (city == null) {
                    _uiState.update {
                        it.copy(errorMessage = "City not found")
                    }
                    return@launch
                }

                // Update UI state with loaded data
                _uiState.value = mealEditorMapper.toUiState(
                    meal = meal,
                    restaurantName = restaurant.name,
                    cityName = city.name,
                    maxPhotoCount = MAX_PHOTO_COUNT
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = resourceProvider.getString(R.string.feature_meal_error_unknown)
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

    fun selectRestaurantSuggestion(suggestion: RestaurantSuggestionUiModel) {
        _uiState.update {
            it.copy(
                restaurantName = suggestion.name,
                restaurantSuggestionList = emptyList()
            )
        }
    }

    fun updateCityName(cityName: String) {
        _uiState.value = _uiState.value.copy(cityName = cityName)
        searchCities(query = cityName)
    }

    private fun searchCities(query: String) {
        viewModelScope.launch {
            try {
                val suggestionList = if (query.length >= 2) {
                    cityRepository.searchByNamePrefix(query = query)
                        .map { mealEditorMapper.toCitySuggestion(city = it) }
                } else {
                    emptyList()
                }
                _uiState.update { it.copy(citySuggestionList = suggestionList) }
            } catch (e: Exception) {
                _uiState.update { it.copy(citySuggestionList = emptyList()) }
            }
        }
    }

    fun selectCitySuggestion(suggestion: CitySuggestionUiModel) {
        _uiState.update {
            it.copy(
                cityName = suggestion.name,
                citySuggestionList = emptyList()
            )
        }
    }

    fun updateMealName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun setIsSomeoneElsePaying(isSomeoneElsePaying: Boolean) {
        _uiState.value = _uiState.value.copy(isSomeoneElsePaying = isSomeoneElsePaying)
    }

    fun addDish(dish: Dish) {
        _uiState.update { state ->
            state.copy(dishList = state.dishList + dish)
        }
    }

    fun updateDish(dishId: Int, updatedDish: Dish) {
        _uiState.update { state ->
            val newList = state.dishList.map { dish ->
                if (dish.id == dishId) updatedDish else dish
            }
            state.copy(dishList = newList)
        }
    }

    fun deleteDish(dishId: Int) {
        _uiState.update { state ->
            state.copy(dishList = state.dishList.filter { it.id != dishId })
        }
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
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        errorMessage = null,
                        fieldErrors = FieldErrors()
                    )
                }

                // Validate inputs and show field errors if any
                val validationErrors = validateInputs()
                if (validationErrors.hasErrors()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            fieldErrors = validationErrors,
                            errorMessage = resourceProvider.getString(R.string.feature_meal_error_fix_above)
                        )
                    }
                    return@launch
                }

                withContext(Dispatchers.IO) {
                    var city = cityRepository.getByName(name = uiState.value.cityName)
                    if (city == null) {
                        city = cityRepository.saveByNameAndGetLocal(name = uiState.value.cityName)
                    }

                    var restaurant = restaurantRepository.getRestaurantByNameAndCityId(
                        name = uiState.value.restaurantName,
                        cityId = city.id
                    )
                    if (restaurant == null) {
                        restaurant = restaurantRepository.saveByNameAndGetLocal(
                            restaurantName = uiState.value.restaurantName,
                            cityId = city.id
                        )
                    }

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

                _uiState.update { it.copy(isLoading = false) }
                onSaveMealSuccess()
            } catch (e: IllegalArgumentException) {
                // Handle validation errors from mapper or other sources
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                            ?: resourceProvider.getString(R.string.feature_meal_error_invalid_input)
                    )
                }
            } catch (e: SQLiteException) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = resourceProvider.getString(
                            R.string.feature_meal_error_db_generic,
                            getDatabaseErrorMessage(e)
                        )
                    )
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                            ?: resourceProvider.getString(R.string.feature_meal_error_invalid_state)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = resourceProvider.getString(
                            R.string.feature_meal_error_save_meal_failed,
                            e.message ?: resourceProvider.getString(R.string.feature_meal_error_unknown)
                        )
                    )
                }
            }
        }
    }

    private fun validateInputs(): FieldErrors {
        val state = uiState.value

        val restaurantNameError = when {
            state.restaurantName.isBlank() -> resourceProvider.getString(R.string.feature_meal_error_restaurant_name_required)
            else -> null
        }

        val cityNameError = when {
            state.cityName.isBlank() -> resourceProvider.getString(R.string.feature_meal_error_city_name_required)
            else -> null
        }

        val mealNameError = when {
            state.name.isBlank() -> resourceProvider.getString(R.string.feature_meal_error_meal_name_required)
            else -> null
        }

        val dishListError = when {
            state.dishList.isEmpty() -> resourceProvider.getString(R.string.feature_meal_error_dish_required)
            else -> null
        }

        return FieldErrors(
            restaurantNameError = restaurantNameError,
            cityNameError = cityNameError,
            mealNameError = mealNameError,
            dishListError = dishListError
        )
    }

    fun clearFieldError(field: MealEditorField) {
        _uiState.update {
            val newFieldErrors = when (field) {
                MealEditorField.RESTAURANT_NAME -> it.fieldErrors.copy(restaurantNameError = null)
                MealEditorField.CITY_NAME -> it.fieldErrors.copy(cityNameError = null)
                MealEditorField.MEAL_NAME -> it.fieldErrors.copy(mealNameError = null)
                MealEditorField.DISH_LIST -> it.fieldErrors.copy(dishListError = null)
            }
            it.copy(fieldErrors = newFieldErrors)
        }
    }

    private fun getDatabaseErrorMessage(e: SQLiteException): String {
        return when {
            e.message?.contains("disk", ignoreCase = true) == true ->
                resourceProvider.getString(R.string.feature_meal_error_db_no_space)

            e.message?.contains("corrupt", ignoreCase = true) == true ->
                resourceProvider.getString(R.string.feature_meal_error_db_corrupted)

            e.message?.contains("lock", ignoreCase = true) == true ->
                resourceProvider.getString(R.string.feature_meal_error_db_locked)

            else -> resourceProvider.getString(R.string.feature_meal_error_db_unable_to_save)
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

    fun selectPhotoForView(uri: Uri) {
        _uiState.update { it.copy(selectedPhotoUri = uri) }
    }

    fun clearSelectedPhoto() {
        _uiState.update { it.copy(selectedPhotoUri = null) }
    }

    fun showDishDialog(dish: Dish?) {
        val dishEditorState = if (dish != null) {
            DishEditorState(
                dishId = dish.id,
                name = dish.name,
                description = dish.description,
                priceString = dish.price.toString(),
                rating = dish.rating,
                dishType = dish.dishType
            )
        } else {
            DishEditorState()
        }

        _uiState.update {
            it.copy(
                showDishDialog = true,
                dishEditorState = dishEditorState
            )
        }
    }

    fun dismissDishDialog() {
        _uiState.update {
            it.copy(
                showDishDialog = false,
                dishEditorState = DishEditorState()
            )
        }
    }

    fun updateDishName(name: String) {
        _uiState.update {
            it.copy(dishEditorState = it.dishEditorState.copy(name = name, nameError = null))
        }
    }

    fun updateDishDescription(description: String) {
        _uiState.update {
            it.copy(dishEditorState = it.dishEditorState.copy(description = description))
        }
    }

    fun updateDishPrice(priceString: String) {
        _uiState.update {
            it.copy(
                dishEditorState = it.dishEditorState.copy(
                    priceString = priceString,
                    priceError = null
                )
            )
        }
    }

    fun updateDishRating(rating: Float) {
        _uiState.update {
            it.copy(dishEditorState = it.dishEditorState.copy(rating = rating, ratingError = null))
        }
    }

    fun updateDishType(dishType: DishType) {
        _uiState.update {
            it.copy(dishEditorState = it.dishEditorState.copy(dishType = dishType))
        }
    }

    fun setDishTypeExpanded(isExpanded: Boolean) {
        _uiState.update {
            it.copy(dishEditorState = it.dishEditorState.copy(isExpanded = isExpanded))
        }
    }

    fun saveDishFromEditor() {
        val state = uiState.value.dishEditorState

        // Validate
        var hasError = false
        var nameError: String? = null
        var priceError: String? = null
        var ratingError: String? = null

        if (state.name.isBlank()) {
            nameError = resourceProvider.getString(R.string.feature_meal_error_dish_name_required)
            hasError = true
        }


        val price =
            if (uiState.value.isSomeoneElsePaying) 0.0 else state.priceString.toDoubleOrNull()
        if (uiState.value.isSomeoneElsePaying) {
            priceError = null
        } else if (state.priceString.isBlank()) {
            priceError = resourceProvider.getString(R.string.feature_meal_error_price_required)
            hasError = true
        } else if (price == null) {
            priceError = resourceProvider.getString(R.string.feature_meal_error_invalid_price_format)
            hasError = true
        } else if (price < 0) {
            priceError = resourceProvider.getString(R.string.feature_meal_error_price_negative)
            hasError = true
        }

        if (state.rating == 0f) {
            ratingError = resourceProvider.getString(R.string.feature_meal_error_rating_required)
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    dishEditorState = it.dishEditorState.copy(
                        nameError = nameError,
                        priceError = priceError,
                        ratingError = ratingError
                    )
                )
            }
            return
        }

        // Create and save dish
        if (price != null) {
            // Check if we're editing an existing dish or adding a new one
            val isEditing = state.dishId != 0

            // For new dishes in the UI, use a unique temporary ID
            // We'll generate new IDs sequentially starting from -1, -2, -3...
            // These will be reset to 0 when saving to database
            val dishId = if (isEditing) {
                state.dishId
            } else {
                // Generate next negative ID for UI tracking
                val minId = uiState.value.dishList.minOfOrNull { it.id } ?: 0
                if (minId <= 0) minId - 1 else -1
            }

            val dish = Dish(
                id = dishId,
                name = state.name,
                rating = state.rating,
                description = state.description,
                price = price,
                dishType = state.dishType
            )

            if (isEditing) {
                updateDish(dishId = state.dishId, updatedDish = dish)
            } else {
                addDish(dish = dish)
            }

            dismissDishDialog()
        }
    }

    companion object {
        const val MAX_PHOTO_COUNT = 5
    }
}
