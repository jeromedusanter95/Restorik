package com.jeromedusanter.restorik.feature.meal.list

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeromedusanter.restorik.core.data.MealRepository
import com.jeromedusanter.restorik.core.data.RestaurantRepository
import com.jeromedusanter.restorik.core.model.Meal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MealListViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    val uiState: StateFlow<MealListUiState> = combine(
        mealRepository.observeAll(),
        restaurantRepository.observeAll()
    ) { mealList, restaurantList ->
        val restaurantMap = restaurantList.associateBy { it.id }
        MealListUiState(
            mealList = mealList.map { meal ->
                val restaurantName = restaurantMap[meal.restaurantId]?.name ?: "Unknown"
                meal.toUiModel(restaurantName = restaurantName)
            },
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = MealListUiState.EMPTY
    )

    private fun Meal.toUiModel(restaurantName: String): MealUiModel {
        return MealUiModel(
            id = id,
            name = name,
            restaurantName = restaurantName,
            date = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            rating = ratingOnFive,
            photoUri = photoList.firstOrNull()?.toUri()
        )
    }
}