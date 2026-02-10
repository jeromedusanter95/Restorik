package com.jeromedusanter.restorik.feature.search

import com.google.common.truth.Truth.assertThat
import com.jeromedusanter.restorik.core.model.RecentSearch
import com.jeromedusanter.restorik.core.testing.MainDispatcherRule
import com.jeromedusanter.restorik.core.testing.data.FakeSearchData
import com.jeromedusanter.restorik.core.testing.repository.TestSearchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: SearchViewModel
    private lateinit var testSearchRepository: TestSearchRepository

    @Before
    fun setup() {
        testSearchRepository = TestSearchRepository()
        viewModel = SearchViewModel(searchRepository = testSearchRepository)
    }

    private fun TestScope.activateUiState() {
        backgroundScope.launch(context = UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
    }

    private fun givenFullSearchData() {
        testSearchRepository.sendMeals(meals = FakeSearchData.meals)
        testSearchRepository.sendRestaurants(restaurants = FakeSearchData.restaurants)
        testSearchRepository.sendCities(cities = FakeSearchData.cities)
    }

    private fun TestScope.search(query: String) {
        viewModel.updateQuery(newQuery = query)
        advanceTimeBy(delayTimeMillis = DEBOUNCE_MS + 100)
    }

    @Test
    fun `initial state is empty`() {
        // GIVEN - ViewModel is created in setup()

        // WHEN
        val state = viewModel.uiState.value

        // THEN
        assertThat(state.query).isEmpty()
        assertThat(state.searchResultList).isEmpty()
        assertThat(state.recentSearchList).isEmpty()
        assertThat(state.isSearching).isFalse()
    }

    @Test
    fun `updateQuery updates query in state`() = runTest {
        // GIVEN
        activateUiState()
        val newQuery = "Pizza"

        // WHEN
        viewModel.updateQuery(newQuery = newQuery)

        // THEN
        assertThat(viewModel.uiState.value.query).isEqualTo(newQuery)
    }

    @Test
    fun `updateQuery with short query does not trigger search`() = runTest {
        // GIVEN
        activateUiState()
        givenFullSearchData()

        // WHEN - Query with only 1 character (less than minimum 2)
        search(query = "P")

        // THEN
        assertThat(viewModel.uiState.value.searchResultList).isEmpty()
        assertThat(viewModel.uiState.value.isSearching).isFalse()
    }

    @Test
    fun `search is case insensitive`() = runTest {
        // GIVEN
        activateUiState()
        givenFullSearchData()

        // WHEN - Search with lowercase
        search(query = "pizza")

        // THEN - Should find "Pizza Palace"
        val state = viewModel.uiState.value
        assertThat(state.searchResultList).isNotEmpty()
        assertThat(state.isSearching).isFalse()
    }

    @Test
    fun `search for restaurant name returns restaurant result`() = runTest {
        // GIVEN
        activateUiState()
        testSearchRepository.sendRestaurants(restaurants = FakeSearchData.restaurants)
        testSearchRepository.sendCities(cities = FakeSearchData.cities)

        // WHEN
        search(query = "Pizza Palace")

        // THEN
        val results = viewModel.uiState.value.searchResultList
        assertThat(results).hasSize(1)
        assertThat(results[0]).isEqualTo(
            SearchResultUiModel.RestaurantItem(
                id = 2,
                name = "Pizza Palace",
                cityName = "Paris"
            )
        )
    }

    @Test
    fun `search for meal name returns meal result`() = runTest {
        // GIVEN
        activateUiState()
        givenFullSearchData()

        // WHEN
        search(query = "Italian Dinner")

        // THEN
        val results = viewModel.uiState.value.searchResultList
        assertThat(results).hasSize(1)

        val mealItem = results[0] as SearchResultUiModel.MealItem
        assertThat(mealItem.name).isEqualTo("Italian Dinner")
        assertThat(mealItem.restaurantName).isEqualTo("Sapore Italiano")
        assertThat(mealItem.cityName).isEqualTo("Paris")
    }

    @Test
    fun `search for dish name returns meal containing dish`() = runTest {
        // GIVEN
        activateUiState()
        givenFullSearchData()

        // WHEN
        search(query = "Carbonara")

        // THEN - Should find Italian Dinner which contains Carbonara
        val results = viewModel.uiState.value.searchResultList
        assertThat(results).isNotEmpty()

        val mealItem = results[0] as SearchResultUiModel.MealItem
        assertThat(mealItem.name).isEqualTo("Italian Dinner")
    }

    @Test
    fun `search with empty query returns empty results`() = runTest {
        // GIVEN
        activateUiState()
        givenFullSearchData()

        // WHEN
        search(query = "")

        // THEN
        assertThat(viewModel.uiState.value.searchResultList).isEmpty()
    }

    @Test
    fun `onSearchSubmitted saves recent search`() = runTest {
        // GIVEN
        activateUiState()
        val query = "Sushi"

        // WHEN
        viewModel.onSearchSubmitted(query = query)
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.uiState.value.recentSearchList).contains(query)
    }

    @Test
    fun `onSearchSubmitted with blank query does not save`() = runTest {
        // GIVEN
        activateUiState()

        // WHEN
        viewModel.onSearchSubmitted(query = "   ")
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.uiState.value.recentSearchList).isEmpty()
    }

    @Test
    fun `onSearchSubmitted with duplicate query saves as newest`() = runTest {
        // GIVEN
        activateUiState()
        val query = "Pizza"
        viewModel.onSearchSubmitted(query = query)
        advanceUntilIdle()
        viewModel.onSearchSubmitted(query = "Sushi")
        advanceUntilIdle()

        // WHEN - Submit same query again
        viewModel.onSearchSubmitted(query = query)
        advanceUntilIdle()

        // THEN - Should be at the top (most recent first)
        val recentSearches = viewModel.uiState.value.recentSearchList
        assertThat(recentSearches).hasSize(2)
        assertThat(recentSearches).containsExactly(query, "Sushi").inOrder()
    }

    @Test
    fun `onRecentSearchClicked updates query and saves search`() = runTest {
        // GIVEN
        activateUiState()
        testSearchRepository.sendRecentSearches(
            searches = listOf(
                RecentSearch(id = 1, query = "Pizza", timestamp = 1L)
            )
        )
        advanceUntilIdle()

        // WHEN
        viewModel.onRecentSearchClicked(query = "Pizza")
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.uiState.value.query).isEqualTo("Pizza")
        assertThat(viewModel.uiState.value.recentSearchList).contains("Pizza")
    }

    @Test
    fun `deleteRecentSearch removes search from list`() = runTest {
        // GIVEN
        activateUiState()
        val query = "Pizza"
        viewModel.onSearchSubmitted(query = query)
        advanceUntilIdle()

        // WHEN
        viewModel.deleteRecentSearch(query = query)
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.uiState.value.recentSearchList).doesNotContain(query)
    }

    @Test
    fun `clearQuery resets query to empty`() = runTest {
        // GIVEN
        activateUiState()
        viewModel.updateQuery(newQuery = "Pizza")
        advanceUntilIdle()

        // WHEN
        viewModel.clearQuery()
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.uiState.value.query).isEmpty()
    }

    @Test
    fun `clearQuery clears search results`() = runTest {
        // GIVEN
        activateUiState()
        testSearchRepository.sendRestaurants(restaurants = FakeSearchData.restaurants)
        testSearchRepository.sendCities(cities = FakeSearchData.cities)
        search(query = "Pizza")

        // WHEN
        viewModel.clearQuery()
        advanceTimeBy(delayTimeMillis = DEBOUNCE_MS + 100)

        // THEN
        assertThat(viewModel.uiState.value.searchResultList).isEmpty()
    }

    @Test
    fun `recent searches display in reverse chronological order`() = runTest {
        // GIVEN
        activateUiState()
        testSearchRepository.sendRecentSearches(
            searches = listOf(
                RecentSearch(id = 1, query = "First", timestamp = 100),
                RecentSearch(id = 2, query = "Second", timestamp = 200),
                RecentSearch(id = 3, query = "Third", timestamp = 300)
            )
        )
        advanceUntilIdle()

        // WHEN
        val recentSearches = viewModel.uiState.value.recentSearchList

        // THEN - Should be in reverse order (newest first)
        assertThat(recentSearches).containsExactly("Third", "Second", "First").inOrder()
    }

    @Test
    fun `recent searches limited to 20`() = runTest {
        // GIVEN
        activateUiState()
        testSearchRepository.sendRecentSearches(
            searches = (1..25).map {
                RecentSearch(id = it, query = "Search $it", timestamp = it.toLong())
            }
        )
        advanceUntilIdle()

        // WHEN
        val recentSearches = viewModel.uiState.value.recentSearchList

        // THEN - Should only show 20 most recent
        assertThat(recentSearches).hasSize(20)
        assertThat(recentSearches[0]).isEqualTo("Search 25") // Most recent first
    }

    @Test
    fun `meal result includes all required fields`() = runTest {
        // GIVEN
        activateUiState()
        givenFullSearchData()

        // WHEN
        search(query = "Italian Dinner")

        // THEN
        val mealItem = viewModel.uiState.value.searchResultList[0] as SearchResultUiModel.MealItem
        assertThat(mealItem.id).isEqualTo(1)
        assertThat(mealItem.name).isEqualTo("Italian Dinner")
        assertThat(mealItem.restaurantName).isEqualTo("Sapore Italiano")
        assertThat(mealItem.cityName).isEqualTo("Paris")
        assertThat(mealItem.rating).isNotNull()
    }

    @Test
    fun `restaurant result includes all required fields`() = runTest {
        // GIVEN
        activateUiState()
        testSearchRepository.sendRestaurants(restaurants = FakeSearchData.restaurants)
        testSearchRepository.sendCities(cities = FakeSearchData.cities)

        // WHEN
        search(query = "Sushi Spot")

        // THEN
        val restaurantItem = viewModel.uiState.value.searchResultList[0] as SearchResultUiModel.RestaurantItem
        assertThat(restaurantItem).isEqualTo(
            SearchResultUiModel.RestaurantItem(
                id = 3,
                name = "Sushi Spot",
                cityName = "Paris"
            )
        )
    }

    @Test
    fun `search with multiple results returns mixed types`() = runTest {
        // GIVEN
        activateUiState()
        givenFullSearchData()

        // WHEN - Search for common term that matches both restaurants and meals
        search(query = "Burger")

        // THEN - Should have both restaurant and meal results
        val results = viewModel.uiState.value.searchResultList
        assertThat(results).isNotEmpty()

        val hasRestaurant = results.any { it is SearchResultUiModel.RestaurantItem }
        val hasMeal = results.any { it is SearchResultUiModel.MealItem }

        assertThat(hasRestaurant).isTrue()
        assertThat(hasMeal).isTrue()
    }

    companion object {
        private const val DEBOUNCE_MS = 300L
    }
}
