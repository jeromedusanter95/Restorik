package com.jeromedusanter.restorik.feature.meal.detail

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.jeromedusanter.restorik.core.testing.MainDispatcherRule
import com.jeromedusanter.restorik.core.testing.data.FakeMealData
import com.jeromedusanter.restorik.core.testing.repository.TestCityRepository
import com.jeromedusanter.restorik.core.testing.repository.TestMealRepository
import com.jeromedusanter.restorik.core.testing.repository.TestRestaurantRepository
import com.jeromedusanter.restorik.core.testing.resources.TestResourceProvider
import com.jeromedusanter.restorik.feature.meal.navigation.MealDestinations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MealDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var testMealRepository: TestMealRepository
    private lateinit var testRestaurantRepository: TestRestaurantRepository
    private lateinit var testCityRepository: TestCityRepository
    private lateinit var mapper: MealDetailMapper
    private lateinit var resourceProvider: TestResourceProvider

    @Before
    fun setup() {
        resourceProvider = TestResourceProvider()
        testMealRepository = TestMealRepository()
        testRestaurantRepository = TestRestaurantRepository()
        testCityRepository = TestCityRepository()
        mapper = MealDetailMapper()
    }

    private fun TestScope.activateUiState(viewModel: MealDetailViewModel) {
        backgroundScope.launch(context = UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
    }

    private fun givenFullMealData() {
        testMealRepository.sendMeals(meals = FakeMealData.meals)
        testRestaurantRepository.sendRestaurants(restaurants = FakeMealData.restaurants)
        testCityRepository.sendCities(cities = FakeMealData.cities)
    }

    private fun createViewModel(mealId: Int): MealDetailViewModel {
        val savedStateHandle = SavedStateHandle().apply {
            set(MealDestinations.MealDetail.mealIdArg, mealId)
        }

        return MealDetailViewModel(
            savedStateHandle = savedStateHandle,
            mealRepository = testMealRepository,
            restaurantRepository = testRestaurantRepository,
            cityRepository = testCityRepository,
            mapper = mapper,
            resourceProvider = resourceProvider
        )
    }

    private fun TestScope.givenViewModelWithData(mealId: Int): MealDetailViewModel {
        givenFullMealData()
        val viewModel = createViewModel(mealId = mealId)
        activateUiState(viewModel = viewModel)
        advanceUntilIdle()
        return viewModel
    }

    @Test
    fun `initial state is empty`() {
        // GIVEN
        givenFullMealData()
        val viewModel = createViewModel(mealId = ITALIAN_DINNER_MEAL_ID)

        // WHEN
        val state = viewModel.uiState.value

        // THEN
        assertThat(state).isEqualTo(MealDetailUiState.EMPTY)
    }

    @Test
    fun `meal detail loads successfully`() = runTest {
        // GIVEN
        val viewModel = givenViewModelWithData(mealId = ITALIAN_DINNER_MEAL_ID)

        // WHEN
        val state = viewModel.uiState.value

        // THEN
        assertThat(state.name).isEqualTo("Italian Dinner")
        assertThat(state.restaurantName).isEqualTo("Sapore Italiano")
        assertThat(state.cityName).isEqualTo("Paris")
        assertThat(state.dishList).hasSize(3)
    }

    @Test
    fun `meal detail includes all dish information`() = runTest {
        // GIVEN
        val viewModel = givenViewModelWithData(mealId = ITALIAN_DINNER_MEAL_ID)

        // WHEN
        val state = viewModel.uiState.value

        // THEN
        assertThat(state.dishList).hasSize(3)
        assertThat(state.dishList[0].name).isEqualTo("Bruschetta")
        assertThat(state.dishList[1].name).isEqualTo("Spaghetti Carbonara")
        assertThat(state.dishList[2].name).isEqualTo("Tiramisu")
    }

    @Test
    fun `meal detail calculates total price correctly`() = runTest {
        // GIVEN
        val viewModel = givenViewModelWithData(mealId = ITALIAN_DINNER_MEAL_ID)

        // WHEN
        val state = viewModel.uiState.value

        // THEN
        // Bruschetta (7.00) + Carbonara (18.50) + Tiramisu (8.50) = 34.00
        // Accept both locale formats (34.00 or 34,00)
        assertThat(state.priceAsString).matches("34[.,]00")
    }

    @Test
    fun `meal detail calculates average rating correctly`() = runTest {
        // GIVEN
        val viewModel = givenViewModelWithData(mealId = ITALIAN_DINNER_MEAL_ID)

        // WHEN
        val state = viewModel.uiState.value

        // THEN
        // Average of ratings: (4.2 + 4.5 + 5.0) / 3 = 4.566...
        assertThat(state.ratingOnFive).isGreaterThan(4.5f)
        assertThat(state.ratingOnFive).isLessThan(4.6f)
    }

    @Test
    fun `meal with isSomeoneElsePaying true reflects in state`() = runTest {
        // GIVEN
        val viewModel = givenViewModelWithData(mealId = SUSHI_FEAST_MEAL_ID)

        // WHEN
        val state = viewModel.uiState.value

        // THEN
        assertThat(state.isSomeoneElsePaying).isTrue()
    }

    @Test
    fun `meal with isSomeoneElsePaying false reflects in state`() = runTest {
        // GIVEN
        val viewModel = givenViewModelWithData(mealId = ITALIAN_DINNER_MEAL_ID)

        // WHEN
        val state = viewModel.uiState.value

        // THEN
        assertThat(state.isSomeoneElsePaying).isFalse()
    }

    @Test
    fun `deleteMeal removes meal from repository`() = runTest {
        // GIVEN
        givenFullMealData()
        val viewModel = createViewModel(mealId = ITALIAN_DINNER_MEAL_ID)

        // Don't activate UI state to avoid flow errors when meal is deleted
        var deleteSuccessCalled = false

        // WHEN
        viewModel.deleteMeal(onSuccess = {
            deleteSuccessCalled = true
        })
        advanceUntilIdle()

        // THEN
        assertThat(deleteSuccessCalled).isTrue()

        // Verify meal is deleted from repository
        val deletedMeal = testMealRepository.getById(id = ITALIAN_DINNER_MEAL_ID)
        assertThat(deletedMeal).isNull()
    }

    @Test
    fun `deleteMeal calls onSuccess callback`() = runTest {
        // GIVEN
        givenFullMealData()
        val viewModel = createViewModel(mealId = ITALIAN_DINNER_MEAL_ID)

        // Don't activate UI state to avoid flow errors when meal is deleted
        var callbackInvoked = false

        // WHEN
        viewModel.deleteMeal(onSuccess = {
            callbackInvoked = true
        })
        advanceUntilIdle()

        // THEN
        assertThat(callbackInvoked).isTrue()
    }

    @Test
    fun `different meals load different data`() = runTest {
        // GIVEN
        givenFullMealData()
        val viewModel1 = createViewModel(mealId = ITALIAN_DINNER_MEAL_ID)
        val viewModel2 = createViewModel(mealId = SUSHI_FEAST_MEAL_ID)
        activateUiState(viewModel = viewModel1)
        activateUiState(viewModel = viewModel2)

        // WHEN
        advanceUntilIdle()

        // THEN
        val state1 = viewModel1.uiState.value
        val state2 = viewModel2.uiState.value

        assertThat(state1.name).isEqualTo("Italian Dinner")
        assertThat(state2.name).isEqualTo("Sushi Feast")
        assertThat(state1.restaurantName).isNotEqualTo(state2.restaurantName)
    }

    @Test
    fun `meal without photos has empty photo list`() = runTest {
        // GIVEN
        val viewModel = givenViewModelWithData(mealId = ITALIAN_DINNER_MEAL_ID)

        // WHEN
        val state = viewModel.uiState.value

        // THEN
        assertThat(state.photoUriList).isEmpty()
    }

    @Test
    fun `meal price formatted with two decimal places`() = runTest {
        // GIVEN
        val viewModel = givenViewModelWithData(mealId = PIZZA_NIGHT_MEAL_ID)

        // WHEN
        val state = viewModel.uiState.value

        // THEN
        // Accept both locale formats (14.00 or 14,00)
        assertThat(state.priceAsString).matches("14[.,]00")
    }

    @Test
    fun `meal with single dish displays correctly`() = runTest {
        // GIVEN
        val viewModel = givenViewModelWithData(mealId = PIZZA_NIGHT_MEAL_ID)

        // WHEN
        val state = viewModel.uiState.value

        // THEN
        assertThat(state.dishList).hasSize(1)
        assertThat(state.dishList[0].name).isEqualTo("Margherita Pizza")
        assertThat(state.ratingOnFive).isEqualTo(4.8f)
    }

    @Test
    fun `meal with multiple dishes from different types`() = runTest {
        // GIVEN
        val viewModel = givenViewModelWithData(mealId = ITALIAN_DINNER_MEAL_ID)

        // WHEN
        val state = viewModel.uiState.value

        // THEN
        val dishTypes = state.dishList.map { it.dishType }.distinct()

        assertThat(dishTypes).hasSize(3)
        assertThat(dishTypes).containsAtLeast(
            com.jeromedusanter.restorik.core.model.DishType.STARTER,
            com.jeromedusanter.restorik.core.model.DishType.MAIN_COURSE,
            com.jeromedusanter.restorik.core.model.DishType.DESSERT
        )
    }

    @Test
    fun `meal updates when repository data changes`() = runTest {
        // GIVEN
        val viewModel = givenViewModelWithData(mealId = ITALIAN_DINNER_MEAL_ID)
        val initialName = viewModel.uiState.value.name

        // WHEN - Update meal in repository
        val updatedMeal = FakeMealData.meals[0].copy(name = "Updated Italian Dinner")
        testMealRepository.saveMealInLocalDb(meal = updatedMeal)
        advanceUntilIdle()

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.name).isEqualTo("Updated Italian Dinner")
        assertThat(state.name).isNotEqualTo(initialName)
    }

    private companion object {
        const val ITALIAN_DINNER_MEAL_ID = 1
        const val SUSHI_FEAST_MEAL_ID = 2
        const val PIZZA_NIGHT_MEAL_ID = 4
    }
}
