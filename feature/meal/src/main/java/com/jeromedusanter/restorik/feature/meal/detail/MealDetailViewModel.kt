package com.jeromedusanter.restorik.feature.meal.detail

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeromedusanter.restorik.core.data.CityRepository
import com.jeromedusanter.restorik.core.data.MealRepository
import com.jeromedusanter.restorik.core.data.RestaurantRepository
import com.jeromedusanter.restorik.feature.meal.R
import com.jeromedusanter.restorik.feature.meal.navigation.MealDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mealRepository: MealRepository,
    restaurantRepository: RestaurantRepository,
    cityRepository: CityRepository,
    mapper: MealDetailMapper,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val mealId: Int = checkNotNull(savedStateHandle[MealDestinations.MealDetail.mealIdArg])

    val uiState: StateFlow<MealDetailUiState> = mealRepository.observeMealById(mealId)
        .flatMapLatest { meal ->
            restaurantRepository.observeById(id = meal.restaurantId)
                .flatMapLatest { restaurant ->
                    flow {
                        val city = cityRepository.getById(id = restaurant.cityId)
                        val restaurantName = restaurant.name
                        val cityName = city?.name ?: context.getString(R.string.feature_meal_unknown_city)
                        emit(mapper.mapToUiModel(meal = meal, restaurantName = restaurantName, cityName = cityName))
                    }
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = MealDetailUiState.EMPTY
        )

    fun deleteMeal(onSuccess: () -> Unit) {
        viewModelScope.launch {
            mealRepository.deleteMeal(id = mealId)
            onSuccess()
        }
    }
}