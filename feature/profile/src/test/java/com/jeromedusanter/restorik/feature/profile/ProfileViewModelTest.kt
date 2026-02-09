package com.jeromedusanter.restorik.feature.profile

import com.google.common.truth.Truth.assertThat
import com.jeromedusanter.restorik.core.model.Dish
import com.jeromedusanter.restorik.core.model.DishType
import com.jeromedusanter.restorik.core.model.Meal
import com.jeromedusanter.restorik.core.model.Restaurant
import com.jeromedusanter.restorik.core.testing.MainDispatcherRule
import com.jeromedusanter.restorik.core.testing.repository.TestMealRepository
import com.jeromedusanter.restorik.core.testing.repository.TestRestaurantRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mealRepository = TestMealRepository()
    private val restaurantRepository = TestRestaurantRepository()

    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setup() {
        viewModel = ProfileViewModel(
            mealRepository = mealRepository,
            restaurantRepository = restaurantRepository
        )
    }

    private fun createTestMeal(
        id: Int,
        restaurantId: Int,
        year: Int,
        month: Int,
        day: Int,
        totalPrice: Double,
    ): Meal {
        return Meal(
            id = id,
            name = "Test Meal $id",
            restaurantId = restaurantId,
            dateTime = LocalDateTime.of(year, month, day, 12, 0),
            photoList = emptyList(),
            dishList = listOf(
                Dish(
                    id = id * 10,
                    name = "Test Dish",
                    rating = 4.0f,
                    description = "Test dish description",
                    price = totalPrice,
                    dishType = DishType.MAIN_COURSE
                )
            ),
            isSomeoneElsePaying = false
        )
    }

    @Test
    fun `initial state is EMPTY and loading`() {
        // GIVEN
        // ViewModel is created in setup()

        // WHEN
        val state = viewModel.uiState.value

        // THEN
        assertThat(state.isLoading).isTrue()
        assertThat(state.monthlySpending).isEqualTo(0.0)
        assertThat(state.previousMonthSpending).isEqualTo(0.0)
        assertThat(state.numberOfMeals).isEqualTo(0)
        assertThat(state.averageRating).isEqualTo(0.0)
        assertThat(state.numberOfRestaurants).isEqualTo(0)
        assertThat(state.newRestaurantsTried).isEqualTo(0)
        assertThat(state.topRestaurantsBySpending).isEmpty()
        assertThat(state.averageMealSpending).isEqualTo(0.0)
    }

    @Test
    fun `state contains correct data from repositories`() = runTest {
        // GIVEN
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        val meals = listOf(
            createTestMeal(id = 1, restaurantId = 1, year = 2024, month = 1, day = 1, totalPrice = 200.0),
            createTestMeal(id = 2, restaurantId = 2, year = 2024, month = 1, day = 15, totalPrice = 150.0),
            createTestMeal(id = 3, restaurantId = 3, year = 2024, month = 1, day = 25, totalPrice = 100.0),
            createTestMeal(id = 4, restaurantId = 1, year = 2024, month = 1, day = 4, totalPrice = 200.0)
        )
        val restaurants = listOf(
            Restaurant(id = 1, name = "Pizza Palace", cityId = 1),
            Restaurant(id = 2, name = "Burger Barn", cityId = 1),
            Restaurant(id = 3, name = "Sushi Spot", cityId = 1)
        )
        mealRepository.sendMeals(meals)
        restaurantRepository.sendRestaurants(restaurants)

        // WHEN
        viewModel.selectMonth(YearMonth.of(2024, 1))

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.monthlySpending).isEqualTo(650.0)
        assertThat(state.numberOfMeals).isEqualTo(4)
        assertThat(state.numberOfRestaurants).isEqualTo(3)
        assertThat(state.averageMealSpending).isEqualTo(162.5)
        assertThat(state.topRestaurantsBySpending).hasSize(3)
        assertThat(state.topRestaurantsBySpending.first().restaurantName).isEqualTo("Pizza Palace")
        assertThat(state.topRestaurantsBySpending.first().totalSpending).isEqualTo(400.0)
        assertThat(state.isLoading).isFalse()
    }

    @Test
    fun `averageMealSpending is zero when no meals exist`() = runTest {
        // GIVEN
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        // WHEN
        viewModel.selectMonth(YearMonth.of(2024, 1))

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.numberOfMeals).isEqualTo(0)
        assertThat(state.averageMealSpending).isEqualTo(0.0)
        assertThat(state.averageMealSpending.isFinite()).isTrue()
    }

    @Test
    fun `topRestaurantsBySpending maps IDs to restaurant names correctly`() = runTest {
        // GIVEN
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        val meals = listOf(
            createTestMeal(id = 1, restaurantId = 1, year = 2024, month = 1, day = 1, totalPrice = 200.0),
            createTestMeal(id = 2, restaurantId = 2, year = 2024, month = 1, day = 15, totalPrice = 150.0)
        )
        val restaurants = listOf(
            Restaurant(id = 1, name = "Pizza Palace", cityId = 1),
            Restaurant(id = 2, name = "Burger Barn", cityId = 1)
        )
        mealRepository.sendMeals(meals)
        restaurantRepository.sendRestaurants(restaurants)

        // WHEN
        viewModel.selectMonth(YearMonth.of(2024, 1))

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.topRestaurantsBySpending).hasSize(2)
        assertThat(state.topRestaurantsBySpending[0].restaurantName).isEqualTo("Pizza Palace")
        assertThat(state.topRestaurantsBySpending[0].totalSpending).isEqualTo(200.0)
        assertThat(state.topRestaurantsBySpending[1].restaurantName).isEqualTo("Burger Barn")
        assertThat(state.topRestaurantsBySpending[1].totalSpending).isEqualTo(150.0)
    }

    @Test
    fun `topRestaurantsBySpending filters out restaurants not found in repository`() = runTest {
        // GIVEN
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        val meals = listOf(
            createTestMeal(id = 1, restaurantId = 1, year = 2024, month = 1, day = 1, totalPrice = 200.0),
            createTestMeal(id = 2, restaurantId = 999, year = 2024, month = 1, day = 15, totalPrice = 150.0)
        )
        val restaurants = listOf(
            Restaurant(id = 1, name = "Pizza Palace", cityId = 1)
        )
        mealRepository.sendMeals(meals)
        restaurantRepository.sendRestaurants(restaurants)

        // WHEN
        viewModel.selectMonth(YearMonth.of(2024, 1))

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.topRestaurantsBySpending).hasSize(1)
        assertThat(state.topRestaurantsBySpending[0].restaurantName).isEqualTo("Pizza Palace")
    }
}
