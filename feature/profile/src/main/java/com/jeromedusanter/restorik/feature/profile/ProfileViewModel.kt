package com.jeromedusanter.restorik.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeromedusanter.restorik.core.data.MealRepository
import com.jeromedusanter.restorik.core.data.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    restaurantRepository: RestaurantRepository
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<ProfileUiState> = _selectedMonth.flatMapLatest { selectedMonth ->
        val selectedMonthStr = selectedMonth.toString()
        val previousMonthStr = selectedMonth.minusMonths(1).toString()

        combine(
            mealRepository.getTotalSpendingForMonth(yearMonth = selectedMonthStr),
            mealRepository.getTotalSpendingForMonth(yearMonth = previousMonthStr),
            mealRepository.getMealCountForMonth(yearMonth = selectedMonthStr),
            mealRepository.getAverageRatingForMonth(yearMonth = selectedMonthStr),
            mealRepository.getUniqueRestaurantCountForMonth(yearMonth = selectedMonthStr),
            mealRepository.getTopRestaurantIdsBySpendingForMonth(yearMonth = selectedMonthStr, limit = 3),
            mealRepository.getFirstMealYearMonth(),
            mealRepository.getNewRestaurantsTriedCount(selectedYearMonth = selectedMonthStr),
            restaurantRepository.observeAll()
        ) { monthlySpending, previousMonthSpending, numberOfMeals, averageRating, numberOfRestaurants,
            topRestaurantIds, minMonth, newRestaurantsTried, restaurantList ->

            // Calculate average meal spending
            val averageMealSpending = if (numberOfMeals > 0) {
                monthlySpending / numberOfMeals
            } else {
                0.0
            }

            // Map restaurant IDs to names for top restaurants
            val restaurantMap = restaurantList.associateBy { it.id }
            val topRestaurantsBySpending = topRestaurantIds.mapNotNull { (restaurantId, spending) ->
                restaurantMap[restaurantId]?.let { restaurant ->
                    RestaurantSpending(
                        restaurantName = restaurant.name,
                        totalSpending = spending
                    )
                }
            }

            ProfileUiState(
                selectedMonth = selectedMonth,
                monthlySpending = monthlySpending,
                previousMonthSpending = previousMonthSpending,
                numberOfMeals = numberOfMeals,
                averageRating = averageRating,
                numberOfRestaurants = numberOfRestaurants,
                newRestaurantsTried = newRestaurantsTried,
                topRestaurantsBySpending = topRestaurantsBySpending,
                averageMealSpending = averageMealSpending,
                isLoading = false,
                minMonth = minMonth ?: YearMonth.now()
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = ProfileUiState.EMPTY
    )

    fun selectMonth(yearMonth: YearMonth) {
        _selectedMonth.value = yearMonth
    }
}
