package com.jeromedusanter.restorik.core.testing.repository

import com.jeromedusanter.restorik.core.data.SearchRepository
import com.jeromedusanter.restorik.core.model.Meal
import com.jeromedusanter.restorik.core.model.RecentSearch
import com.jeromedusanter.restorik.core.model.Restaurant
import com.jeromedusanter.restorik.core.model.SearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class TestSearchRepository : SearchRepository {

    /**
     * Internal storage for test data
     */
    private val mealsFlow = MutableStateFlow<List<Meal>>(emptyList())
    private val restaurantsFlow = MutableStateFlow<List<Restaurant>>(emptyList())
    private val citiesFlow = MutableStateFlow<Map<Int, String>>(emptyMap())
    private val recentSearchesFlow = MutableStateFlow<List<RecentSearch>>(emptyList())
    private var recentSearchIdCounter = 1
    private var timestampCounter = 1L

    /**
     * Send test data to the repository
     * All Flows will automatically update based on this data
     */
    fun sendMeals(meals: List<Meal>) {
        mealsFlow.value = meals
    }

    fun sendRestaurants(restaurants: List<Restaurant>) {
        restaurantsFlow.value = restaurants
    }

    fun sendCities(cities: Map<Int, String>) {
        citiesFlow.value = cities
    }

    fun sendRecentSearches(searches: List<RecentSearch>) {
        recentSearchesFlow.value = searches
        // Update counter to avoid conflicts
        recentSearchIdCounter = (searches.maxOfOrNull { it.id } ?: 0) + 1
    }

    override fun search(query: String): Flow<List<SearchResult>> = flow {
        if (query.isBlank()) {
            emit(emptyList())
            return@flow
        }

        val meals = mealsFlow.value
        val restaurants = restaurantsFlow.value
        val cities = citiesFlow.value

        val matchingRestaurants = restaurants.filter {
            it.name.contains(other = query, ignoreCase = true)
        }

        val matchingDishes = meals.flatMap { meal ->
            meal.dishList.filter { dish ->
                dish.name.contains(other = query, ignoreCase = true) ||
                    dish.description.contains(other = query, ignoreCase = true)
            }.map { meal }
        }.distinct()

        val matchingMeals = meals.filter {
            it.name.contains(other = query, ignoreCase = true)
        }

        val results = mutableListOf<SearchResult>()

        // Add restaurant results
        results.addAll(
            matchingRestaurants.map { restaurant ->
                SearchResult.RestaurantResult(
                    restaurant = restaurant,
                    cityName = cities[restaurant.cityId] ?: "Unknown"
                )
            }
        )

        // Combine and deduplicate meal results
        val allMatchingMeals = (matchingDishes + matchingMeals).distinctBy { it.id }

        results.addAll(
            allMatchingMeals.map { meal ->
                val restaurant = restaurants.find { it.id == meal.restaurantId }
                SearchResult.MealResult(
                    meal = meal,
                    restaurantName = restaurant?.name ?: "Unknown",
                    cityName = restaurant?.let { cities[it.cityId] } ?: "Unknown"
                )
            }
        )

        emit(results)
    }

    override fun observeRecentSearches(limit: Int): Flow<List<RecentSearch>> {
        return recentSearchesFlow.map { searches ->
            searches.sortedByDescending { it.timestamp }.take(n = limit)
        }
    }

    override suspend fun saveRecentSearch(query: String) {
        val currentSearches = recentSearchesFlow.value.toMutableList()

        // Remove existing entry with same query to avoid duplicates
        currentSearches.removeIf { it.query == query }

        currentSearches.add(
            RecentSearch(
                id = recentSearchIdCounter++,
                query = query,
                timestamp = timestampCounter++ // Use counter instead of System.currentTimeMillis() for deterministic tests
            )
        )

        recentSearchesFlow.value = currentSearches
    }

    override suspend fun deleteRecentSearch(query: String) {
        recentSearchesFlow.value = recentSearchesFlow.value.filter { it.query != query }
    }

    override suspend fun clearRecentSearches() {
        recentSearchesFlow.value = emptyList()
    }
}
