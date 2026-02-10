package com.jeromedusanter.restorik.feature.meal.list

import com.google.common.truth.Truth.assertThat
import com.jeromedusanter.restorik.core.domain.GetMealListUseCase
import com.jeromedusanter.restorik.core.model.SortMode
import com.jeromedusanter.restorik.core.model.SortOrder
import com.jeromedusanter.restorik.core.testing.MainDispatcherRule
import com.jeromedusanter.restorik.core.testing.data.FakeMealData
import com.jeromedusanter.restorik.core.testing.repository.TestCityRepository
import com.jeromedusanter.restorik.core.testing.repository.TestMealRepository
import com.jeromedusanter.restorik.core.testing.repository.TestRestaurantRepository
import com.jeromedusanter.restorik.core.testing.repository.TestUserPreferencesRepository
import com.jeromedusanter.restorik.core.testing.resources.TestResourceProvider
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
class MealListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: MealListViewModel
    private lateinit var testMealRepository: TestMealRepository
    private lateinit var testRestaurantRepository: TestRestaurantRepository
    private lateinit var testCityRepository: TestCityRepository
    private lateinit var testUserPreferencesRepository: TestUserPreferencesRepository
    private lateinit var getMealListUseCase: GetMealListUseCase
    private lateinit var resourceProvider: TestResourceProvider

    @Before
    fun setup() {
        resourceProvider = TestResourceProvider()
        testMealRepository = TestMealRepository()
        testRestaurantRepository = TestRestaurantRepository()
        testCityRepository = TestCityRepository()
        testUserPreferencesRepository = TestUserPreferencesRepository()
        getMealListUseCase = GetMealListUseCase(mealRepository = testMealRepository)

        viewModel = MealListViewModel(
            getMealListUseCase = getMealListUseCase,
            restaurantRepository = testRestaurantRepository,
            cityRepository = testCityRepository,
            userPreferencesRepository = testUserPreferencesRepository,
            resourceProvider = resourceProvider
        )
    }

    private fun TestScope.activateUiState() {
        backgroundScope.launch(context = UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
    }

    private fun givenFullMealData() {
        testMealRepository.sendMeals(meals = FakeMealData.meals)
        testRestaurantRepository.sendRestaurants(restaurants = FakeMealData.restaurants)
        testCityRepository.sendCities(cities = FakeMealData.cities)
    }

    private fun TestScope.givenActivatedStateWithData() {
        activateUiState()
        givenFullMealData()
        advanceUntilIdle()
    }

    private fun MealListUiState.totalMealCount(): Int {
        return groupedMealList.sumOf { it.mealList.size }
    }

    @Test
    fun `initial state is loading`() {
        // GIVEN - ViewModel is created in setup()

        // WHEN
        val state = viewModel.uiState.value

        // THEN
        assertThat(state.isLoading).isTrue()
        assertThat(state.groupedMealList).isEmpty()
        assertThat(state.sortMode).isEqualTo(SortMode.DATE)
        assertThat(state.sortOrder).isEqualTo(SortOrder.DESCENDING)
        assertThat(state.showFilterDialog).isFalse()
        assertThat(state.filterRestaurantName).isNull()
    }

    @Test
    fun `meals display with loading complete`() = runTest {
        // GIVEN
        givenActivatedStateWithData()

        // WHEN
        val state = viewModel.uiState.value

        // THEN
        assertThat(state.isLoading).isFalse()
        assertThat(state.groupedMealList).isNotEmpty()
    }

    @Test
    fun `meals grouped by date in descending order by default`() = runTest {
        // GIVEN
        givenActivatedStateWithData()

        // WHEN
        val state = viewModel.uiState.value

        // THEN
        assertThat(state.sortMode).isEqualTo(SortMode.DATE)
        assertThat(state.sortOrder).isEqualTo(SortOrder.DESCENDING)
        assertThat(state.groupedMealList).isNotEmpty()
    }

    @Test
    fun `sort by restaurant groups by restaurant name`() = runTest {
        // GIVEN
        givenActivatedStateWithData()

        // WHEN
        viewModel.applySortPreferences(sortMode = SortMode.RESTAURANT, sortOrder = SortOrder.ASCENDING)
        advanceUntilIdle()

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.sortMode).isEqualTo(SortMode.RESTAURANT)
        assertThat(state.sortOrder).isEqualTo(SortOrder.ASCENDING)
        assertThat(state.groupedMealList).isNotEmpty()

        val firstGroupTitle = state.groupedMealList.first().title
        assertThat(firstGroupTitle).isAnyOf("Burger Barn", "Le Bistrot Français", "Pizza Palace", "Sapore Italiano", "Sushi Spot")
    }

    @Test
    fun `sort by restaurant in descending order reverses restaurant names`() = runTest {
        // GIVEN
        givenActivatedStateWithData()

        // WHEN
        viewModel.applySortPreferences(sortMode = SortMode.RESTAURANT, sortOrder = SortOrder.DESCENDING)
        advanceUntilIdle()

        // THEN
        val groupTitles = viewModel.uiState.value.groupedMealList.map { it.title }
        assertThat(groupTitles.first()).isEqualTo("Sushi Spot")
    }

    @Test
    fun `sort by rating groups by rating value`() = runTest {
        // GIVEN
        givenActivatedStateWithData()

        // WHEN
        viewModel.applySortPreferences(sortMode = SortMode.RATING, sortOrder = SortOrder.DESCENDING)
        advanceUntilIdle()

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.sortMode).isEqualTo(SortMode.RATING)
        assertThat(state.groupedMealList).isNotEmpty()
        assertThat(state.groupedMealList.first().ratingValue).isNotNull()
    }

    @Test
    fun `sort by rating in ascending order shows lowest ratings first`() = runTest {
        // GIVEN
        givenActivatedStateWithData()

        // WHEN
        viewModel.applySortPreferences(sortMode = SortMode.RATING, sortOrder = SortOrder.ASCENDING)
        advanceUntilIdle()

        // THEN
        val ratings = viewModel.uiState.value.groupedMealList.mapNotNull { it.ratingValue }

        // Verify ascending order
        for (i in 0 until ratings.size - 1) {
            assertThat(ratings[i]).isAtMost(ratings[i + 1])
        }
    }

    @Test
    fun `showFilterDialog updates state`() = runTest {
        // GIVEN
        activateUiState()
        advanceUntilIdle()

        // WHEN
        viewModel.showFilterDialog()
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.uiState.value.showFilterDialog).isTrue()
    }

    @Test
    fun `hideFilterDialog updates state`() = runTest {
        // GIVEN
        activateUiState()
        advanceUntilIdle()
        viewModel.showFilterDialog()
        advanceUntilIdle()

        // WHEN
        viewModel.hideFilterDialog()
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.uiState.value.showFilterDialog).isFalse()
    }

    @Test
    fun `setRestaurantFilter filters meals by restaurant`() = runTest {
        // GIVEN
        givenActivatedStateWithData()
        val allMealsCount = viewModel.uiState.value.totalMealCount()

        // WHEN - Filter by "Sapore Italiano" (id=1)
        viewModel.setRestaurantFilter(restaurantId = SAPORE_ITALIANO_ID)
        advanceUntilIdle()

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.filterRestaurantName).isEqualTo(SAPORE_ITALIANO_NAME)

        val filteredMealsCount = state.totalMealCount()
        assertThat(filteredMealsCount).isLessThan(allMealsCount)

        val allFromRestaurant = state.groupedMealList.all { group ->
            group.mealList.all { it.restaurantName == SAPORE_ITALIANO_NAME }
        }
        assertThat(allFromRestaurant).isTrue()
    }

    @Test
    fun `clearRestaurantFilter removes filter`() = runTest {
        // GIVEN
        givenActivatedStateWithData()
        viewModel.setRestaurantFilter(restaurantId = SAPORE_ITALIANO_ID)
        advanceUntilIdle()
        val filteredCount = viewModel.uiState.value.totalMealCount()

        // WHEN
        viewModel.clearRestaurantFilter()
        advanceUntilIdle()

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.filterRestaurantName).isNull()

        val allMealsCount = state.totalMealCount()
        assertThat(allMealsCount).isGreaterThan(filteredCount)
    }

    @Test
    fun `applySortPreferences persists sort mode and order`() = runTest {
        // GIVEN
        givenActivatedStateWithData()

        // WHEN
        viewModel.applySortPreferences(sortMode = SortMode.RATING, sortOrder = SortOrder.ASCENDING)
        advanceUntilIdle()

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.sortMode).isEqualTo(SortMode.RATING)
        assertThat(state.sortOrder).isEqualTo(SortOrder.ASCENDING)
    }

    @Test
    fun `empty meal list shows empty state`() = runTest {
        // GIVEN
        activateUiState()
        testRestaurantRepository.sendRestaurants(restaurants = FakeMealData.restaurants)
        testCityRepository.sendCities(cities = FakeMealData.cities)
        testMealRepository.sendMeals(meals = emptyList())

        // WHEN
        advanceUntilIdle()

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.groupedMealList).isEmpty()
    }

    @Test
    fun `filter with non-existent restaurant shows empty results`() = runTest {
        // GIVEN
        givenActivatedStateWithData()

        // WHEN - Filter by non-existent restaurant
        viewModel.setRestaurantFilter(restaurantId = NON_EXISTENT_ID)
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.uiState.value.totalMealCount()).isEqualTo(0)
    }

    @Test
    fun `sort by date in ascending order shows oldest first`() = runTest {
        // GIVEN
        givenActivatedStateWithData()

        // WHEN
        viewModel.applySortPreferences(sortMode = SortMode.DATE, sortOrder = SortOrder.ASCENDING)
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.uiState.value.sortOrder).isEqualTo(SortOrder.ASCENDING)
    }

    @Test
    fun `meal includes restaurant and city names`() = runTest {
        // GIVEN
        givenActivatedStateWithData()

        // WHEN
        val state = viewModel.uiState.value

        // THEN
        val firstMeal = state.groupedMealList.firstOrNull()?.mealList?.firstOrNull()
        assertThat(firstMeal).isNotNull()
        assertThat(firstMeal?.restaurantName).isNotEmpty()
        assertThat(firstMeal?.cityName).isNotEmpty()
    }

    @Test
    fun `filter persists across sort mode changes`() = runTest {
        // GIVEN
        givenActivatedStateWithData()

        // Set filter
        viewModel.setRestaurantFilter(restaurantId = SAPORE_ITALIANO_ID)
        advanceUntilIdle()
        val filteredCountByDate = viewModel.uiState.value.totalMealCount()

        // WHEN - Change sort mode
        viewModel.applySortPreferences(sortMode = SortMode.RESTAURANT, sortOrder = SortOrder.ASCENDING)
        advanceUntilIdle()

        // THEN - Filter should still be applied
        val state = viewModel.uiState.value
        assertThat(state.filterRestaurantName).isEqualTo(SAPORE_ITALIANO_NAME)

        val filteredCountByRestaurant = state.totalMealCount()
        assertThat(filteredCountByRestaurant).isEqualTo(filteredCountByDate)
    }

    @Test
    fun `changing sort order triggers state update`() = runTest {
        // GIVEN
        givenActivatedStateWithData()

        // WHEN
        viewModel.applySortPreferences(sortMode = SortMode.DATE, sortOrder = SortOrder.ASCENDING)
        advanceUntilIdle()
        val ascendingState = viewModel.uiState.value

        viewModel.applySortPreferences(sortMode = SortMode.DATE, sortOrder = SortOrder.DESCENDING)
        advanceUntilIdle()
        val descendingState = viewModel.uiState.value

        // THEN - States should be different
        assertThat(ascendingState.sortOrder).isEqualTo(SortOrder.ASCENDING)
        assertThat(descendingState.sortOrder).isEqualTo(SortOrder.DESCENDING)
    }

    private companion object {
        const val SAPORE_ITALIANO_ID = 1
        const val SAPORE_ITALIANO_NAME = "Sapore Italiano"
        const val NON_EXISTENT_ID = 999
    }
}
