package com.jeromedusanter.restorik.feature.meal.list

import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeromedusanter.restorik.core.data.RestaurantRepository
import com.jeromedusanter.restorik.core.domain.GetMealListUseCase
import com.jeromedusanter.restorik.core.model.Meal
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
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MealListViewModel @Inject constructor(
    getMealListUseCase: GetMealListUseCase,
    restaurantRepository: RestaurantRepository,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {

    private val _sortMode = MutableStateFlow(SortMode.DATE)
    private val _sortOrder = MutableStateFlow(SortOrder.DESCENDING)
    private val _showFilterDialog = MutableStateFlow(false)
    private val _filterRestaurantId = MutableStateFlow<Int?>(value = null)

    val uiState: StateFlow<MealListUiState> =
        _filterRestaurantId.flatMapLatest { filterRestaurantId ->
            combine(
                getMealListUseCase(),
                restaurantRepository.observeAll(),
                _sortMode,
                _sortOrder,
                _showFilterDialog
            ) { groupedMealList, restaurantList, sortMode, sortOrder, showFilterDialog ->
                val restaurantMap = restaurantList.associateBy { it.id }
                val unknownRestaurant = context.getString(R.string.feature_meal_unknown_restaurant)

                // Flatten all meals from all groups
                val allMeals = groupedMealList.flatMap { it.mealList }

                // Apply restaurant filter if set
                val filteredMeals = if (filterRestaurantId != null) {
                    allMeals.filter { it.restaurantId == filterRestaurantId }
                } else {
                    allMeals
                }

                val filterRestaurantName = if (filterRestaurantId != null) {
                    restaurantMap[filterRestaurantId]?.name
                } else {
                    null
                }

                // Apply sorting and grouping based on sort mode
                val groupedMealUiModelList = when (sortMode) {
                    SortMode.DATE -> {
                        // Use existing date-based grouping and filter within each group
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
                                        val restaurantName = restaurantMap[meal.restaurantId]?.name
                                            ?: unknownRestaurant
                                        meal.toUiModel(restaurantName = restaurantName)
                                    }
                                )
                            }
                        }
                    }

                    SortMode.RESTAURANT -> {
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
                                        meal.toUiModel(restaurantName = restaurantName)
                                    }
                            )
                        }
                    }

                    SortMode.RATING -> {
                        // Group by rating
                        val grouped = filteredMeals.groupBy { it.ratingOnFive }

                        val sortedGroups = if (sortOrder == SortOrder.ASCENDING) {
                            grouped.toSortedMap()
                        } else {
                            grouped.toSortedMap(compareByDescending { it })
                        }

                        sortedGroups.map { (rating, meals) ->
                            MealGroupUiModel(
                                title = "",
                                ratingValue = rating,
                                mealList = meals
                                    .sortedByDescending { it.dateTime }
                                    .map { meal ->
                                        val restaurantName = restaurantMap[meal.restaurantId]?.name
                                            ?: unknownRestaurant
                                        meal.toUiModel(restaurantName = restaurantName)
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
        _sortMode.update { sortMode }
        _sortOrder.update { sortOrder }
    }

    fun setRestaurantFilter(restaurantId: Int?) {
        _filterRestaurantId.update { restaurantId }
    }

    fun clearRestaurantFilter() {
        _filterRestaurantId.update { null }
    }
}