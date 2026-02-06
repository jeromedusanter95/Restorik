package com.jeromedusanter.restorik.feature.meal.list

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeromedusanter.restorik.core.data.CityRepository
import com.jeromedusanter.restorik.core.data.RestaurantRepository
import com.jeromedusanter.restorik.core.data.UserPreferencesRepository
import com.jeromedusanter.restorik.core.domain.GetMealListUseCase
import com.jeromedusanter.restorik.core.model.SortMode
import com.jeromedusanter.restorik.core.model.SortOrder
import com.jeromedusanter.restorik.feature.meal.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealListViewModel @Inject constructor(
    getMealListUseCase: GetMealListUseCase,
    restaurantRepository: RestaurantRepository,
    cityRepository: CityRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {

    private val _showFilterDialog = MutableStateFlow(false)
    private val _filterRestaurantId = MutableStateFlow<Int?>(value = null)

    val uiState: StateFlow<MealListUiState> =
        _filterRestaurantId.flatMapLatest { filterRestaurantId -> // use flatMapLatest for performance, only the last emission is counter the others are cancelled
            combine(
                combine(
                    getMealListUseCase(),
                    restaurantRepository.observeAll(),
                    cityRepository.observeAll()
                ) { groupedMealList, restaurantList, cityList ->
                    Triple(groupedMealList, restaurantList, cityList)
                },
                userPreferencesRepository.sortMode,
                userPreferencesRepository.sortOrder,
                _showFilterDialog
            ) { data, sortMode, sortOrder, showFilterDialog ->
                val (groupedMealList, restaurantList, cityList) = data
                val restaurantMap = restaurantList.associateBy { it.id }
                val cityMap = cityList.associateBy { it.id }
                val unknownRestaurant = context.getString(R.string.feature_meal_unknown_restaurant)
                val unknownCity = context.getString(R.string.feature_meal_unknown_city)

                val filterRestaurantName = if (filterRestaurantId != null) {
                    restaurantMap[filterRestaurantId]?.name
                } else {
                    null
                }

                val groupedMealUiModelList = when (sortMode) {
                    SortMode.DATE -> {
                        groupedMealList.mapNotNull { mealGroup ->
                            // Filter meals within this group if restaurant filter is active
                            val mealsInGroup = if (filterRestaurantId != null) {
                                mealGroup.mealList.filter { it.restaurantId == filterRestaurantId }
                            } else {
                                mealGroup.mealList
                            }

                            // Skip empty groups
                            if (mealsInGroup.isEmpty()) {
                                null
                            } else {
                                val sortedMeals = if (sortOrder == SortOrder.ASCENDING) {
                                    mealsInGroup.sortedBy { it.dateTime }
                                } else {
                                    mealsInGroup.sortedByDescending { it.dateTime }
                                }

                                MealGroupUiModel(
                                    title = mealGroup.groupDate.toLocalizedString(context = context),
                                    mealList = sortedMeals.map { meal ->
                                        val restaurant = restaurantMap[meal.restaurantId]
                                        val restaurantName = restaurant?.name ?: unknownRestaurant
                                        val cityName = restaurant?.let { cityMap[it.cityId]?.name } ?: unknownCity
                                        meal.toUiModel(restaurantName = restaurantName, cityName = cityName)
                                    }
                                )
                            }
                        }
                    }

                    SortMode.RESTAURANT -> {
                        val allMeals = groupedMealList.flatMap { it.mealList }
                        val filteredMeals = if (filterRestaurantId != null) {
                            allMeals.filter { it.restaurantId == filterRestaurantId }
                        } else {
                            allMeals
                        }

                        // Group by restaurant name
                        val grouped = filteredMeals.groupBy { meal ->
                            restaurantMap[meal.restaurantId]?.name ?: unknownRestaurant
                        }

                        val sortedGroups = if (sortOrder == SortOrder.ASCENDING) {
                            grouped.toSortedMap(String.CASE_INSENSITIVE_ORDER)
                        } else {
                            grouped.toSortedMap(String.CASE_INSENSITIVE_ORDER.reversed())
                        }

                        sortedGroups.map { (restaurantName, meals) ->
                            MealGroupUiModel(
                                title = restaurantName,
                                mealList = meals
                                    .sortedByDescending { it.dateTime }
                                    .map { meal ->
                                        val restaurant = restaurantMap[meal.restaurantId]
                                        val cityName = restaurant?.let { cityMap[it.cityId]?.name } ?: unknownCity
                                        meal.toUiModel(restaurantName = restaurantName, cityName = cityName)
                                    }
                            )
                        }
                    }

                    SortMode.RATING -> {
                        val allMeals = groupedMealList.flatMap { it.mealList }
                        val filteredMeals = if (filterRestaurantId != null) {
                            allMeals.filter { it.restaurantId == filterRestaurantId }
                        } else {
                            allMeals
                        }

                        // Group by rating
                        val grouped = filteredMeals.groupBy { it.ratingOnFive }

                        val sortedEntries = if (sortOrder == SortOrder.ASCENDING) {
                            grouped.entries.sortedBy { it.key }
                        } else {
                            grouped.entries.sortedByDescending { it.key }
                        }

                        sortedEntries.map { entry ->
                            MealGroupUiModel(
                                title = "",
                                ratingValue = entry.key,
                                mealList = entry.value
                                    .sortedByDescending { it.dateTime }
                                    .map { meal ->
                                        val restaurant = restaurantMap[meal.restaurantId]
                                        val restaurantName = restaurant?.name ?: unknownRestaurant
                                        val cityName = restaurant?.let { cityMap[it.cityId]?.name } ?: unknownCity
                                        meal.toUiModel(restaurantName = restaurantName, cityName = cityName)
                                    }
                            )
                        }
                    }
                }

                MealListUiState(
                    groupedMealList = groupedMealUiModelList,
                    // isLoading is false here because this state is only emitted after data has been loaded.
                    // Initial loading state is handled by MealListUiState.EMPTY (used as initialValue in stateIn).
                    isLoading = false,
                    sortMode = sortMode,
                    sortOrder = sortOrder,
                    showFilterDialog = showFilterDialog,
                    filterRestaurantName = filterRestaurantName,
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = MealListUiState.EMPTY
        )

    fun showFilterDialog() {
        _showFilterDialog.update { true }
    }

    fun hideFilterDialog() {
        _showFilterDialog.update { false }
    }

    fun applySortPreferences(sortMode: SortMode, sortOrder: SortOrder) {
        viewModelScope.launch {
            userPreferencesRepository.setSortMode(sortMode = sortMode)
            userPreferencesRepository.setSortOrder(sortOrder = sortOrder)
        }
    }

    fun setRestaurantFilter(restaurantId: Int?) {
        _filterRestaurantId.update { restaurantId }
    }

    fun clearRestaurantFilter() {
        _filterRestaurantId.update { null }
    }
}