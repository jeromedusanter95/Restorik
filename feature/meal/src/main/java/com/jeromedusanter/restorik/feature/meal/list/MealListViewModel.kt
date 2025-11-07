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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MealListViewModel @Inject constructor(
    getMealListUseCase: GetMealListUseCase,
    restaurantRepository: RestaurantRepository,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {

    val uiState: StateFlow<MealListUiState> = combine(
        getMealListUseCase(),
        restaurantRepository.observeAll()
    ) { groupedMealList, restaurantList ->
        val restaurantMap = restaurantList.associateBy { it.id }
        val unknownRestaurant = context.getString(R.string.feature_meal_unknown_restaurant)
        val groupedMealUiModelList = groupedMealList.map { mealGroup ->
            MealGroupUiModel(
                title = mealGroup.groupDate.toLocalizedString(context = context),
                mealList = mealGroup.mealList.map { meal ->
                    val restaurantName = restaurantMap[meal.restaurantId]?.name ?: unknownRestaurant
                    meal.toUiModel(restaurantName = restaurantName)
                }
            )
        }

        MealListUiState(
            groupedMealList = groupedMealUiModelList,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = MealListUiState.EMPTY
    )
}