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
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<ProfileUiState> = combine(
        _selectedMonth,
        mealRepository.observeAll(),
        restaurantRepository.observeAll()
    ) { selectedMonth, mealList, restaurantList ->
        val monthlyMeals = mealList.filter { meal ->
            YearMonth.from(meal.dateTime) == selectedMonth
        }
        val totalSpending = monthlyMeals.sumOf { it.price }
        val numberOfMeals = monthlyMeals.size
        val uniqueRestaurantIds = monthlyMeals.map { it.restaurantId }.distinct()
        val numberOfRestaurants = uniqueRestaurantIds.size

        // Calculate previous month spending
        val previousMonth = selectedMonth.minusMonths(1)
        val previousMonthMeals = mealList.filter { meal ->
            YearMonth.from(meal.dateTime) == previousMonth
        }
        val previousMonthSpending = previousMonthMeals.sumOf { it.price }

        // Calculate average rating
        val averageRating = if (numberOfMeals > 0) {
            monthlyMeals.map { it.ratingOnFive }.average()
        } else {
            0.0
        }

        // Calculate new restaurants tried this month
        val restaurantsVisitedBeforeSelectedMonth = mealList
            .filter { meal -> YearMonth.from(meal.dateTime) < selectedMonth }
            .map { it.restaurantId }
            .distinct()
        val newRestaurantsTried = uniqueRestaurantIds.count { restaurantId ->
            restaurantId !in restaurantsVisitedBeforeSelectedMonth
        }

        // Calculate spending per restaurant
        val restaurantSpendingMap = monthlyMeals.groupBy { it.restaurantId }
            .mapValues { (_, meals) -> meals.sumOf { it.price } }

        // Get top 3 restaurants by spending
        val topRestaurantsBySpending = restaurantSpendingMap.entries
            .sortedByDescending { it.value }
            .take(3)
            .mapNotNull { (restaurantId, spending) ->
                val restaurant = restaurantList.find { it.id == restaurantId }
                restaurant?.let {
                    RestaurantSpending(
                        restaurantName = it.name,
                        totalSpending = spending
                    )
                }
            }

        // Calculate average meal spending
        val averageMealSpending = if (numberOfMeals > 0) {
            totalSpending / numberOfMeals
        } else {
            0.0
        }

        ProfileUiState(
            selectedMonth = selectedMonth,
            monthlySpending = totalSpending,
            previousMonthSpending = previousMonthSpending,
            numberOfMeals = numberOfMeals,
            averageRating = averageRating,
            numberOfRestaurants = numberOfRestaurants,
            newRestaurantsTried = newRestaurantsTried,
            topRestaurantsBySpending = topRestaurantsBySpending,
            averageMealSpending = averageMealSpending,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = ProfileUiState.EMPTY
    )

    fun selectMonth(yearMonth: YearMonth) {
        _selectedMonth.value = yearMonth
    }
}
