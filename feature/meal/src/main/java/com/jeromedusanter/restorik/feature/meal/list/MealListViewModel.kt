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

    val uiState: StateFlow<MealListUiState> = combine(
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

        // Apply sorting and grouping based on sort mode
        val groupedMealUiModelList = when (sortMode) {
            SortMode.DATE -> {
                // Use existing date-based grouping
                groupedMealList.map { mealGroup ->
                    val sortedMeals = if (sortOrder == SortOrder.ASCENDING) {
                        mealGroup.mealList.sortedBy { it.dateTime }
                    } else {
                        mealGroup.mealList.sortedByDescending { it.dateTime }
                    }

                    MealGroupUiModel(
                        title = mealGroup.groupDate.toLocalizedString(context = context),
                        mealList = sortedMeals.map { meal ->
                            val restaurantName = restaurantMap[meal.restaurantId]?.name ?: unknownRestaurant
                            meal.toUiModel(restaurantName = restaurantName)
                        }
                    )
                }
            }

            SortMode.RESTAURANT -> {
                // Group by restaurant name
                val grouped = allMeals.groupBy { meal ->
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
                val grouped = allMeals.groupBy { it.ratingOnFive }

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
                                val restaurantName = restaurantMap[meal.restaurantId]?.name ?: unknownRestaurant
                                meal.toUiModel(restaurantName = restaurantName)
                            }
                    )
                }
            }
        }

        MealListUiState(
            groupedMealList = groupedMealUiModelList,
            isLoading = false,
            sortMode = sortMode,
            sortOrder = sortOrder,
            showFilterDialog = showFilterDialog
        )
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
}