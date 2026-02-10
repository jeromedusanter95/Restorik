package com.jeromedusanter.restorik.core.testing.repository

import com.jeromedusanter.restorik.core.data.RestaurantRepository
import com.jeromedusanter.restorik.core.model.Restaurant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map


class TestRestaurantRepository : RestaurantRepository {

    /**
     * Internal storage for restaurants
     */
    private val restaurantsFlow = MutableStateFlow<List<Restaurant>>(emptyList())
    private var nextId = 1

    /**
     * Send test data to the repository
     * All Flows will automatically update based on this data
     */
    fun sendRestaurants(restaurants: List<Restaurant>) {
        restaurantsFlow.value = restaurants
        // Update nextId to avoid conflicts
        nextId = (restaurants.maxOfOrNull { it.id } ?: 0) + 1
    }

    override suspend fun getById(id: Int): Restaurant? {
        return restaurantsFlow.value.find { it.id == id }
    }

    override fun observeById(id: Int): Flow<Restaurant> {
        return restaurantsFlow.map { restaurants ->
            restaurants.find { it.id == id } ?: error("Restaurant with id $id not found")
        }
    }

    override fun observeAll(): Flow<List<Restaurant>> {
        return restaurantsFlow
    }

    override suspend fun saveByNameAndGetLocal(restaurantName: String, cityId: Int): Restaurant {
        // Check if already exists
        val existing = restaurantsFlow.value.find {
            it.name == restaurantName && it.cityId == cityId
        }

        if (existing != null) {
            return existing
        }

        // Create new restaurant
        val newRestaurant = Restaurant(
            id = nextId++,
            name = restaurantName,
            cityId = cityId
        )

        val currentRestaurants = restaurantsFlow.value.toMutableList()
        currentRestaurants.add(newRestaurant)
        restaurantsFlow.value = currentRestaurants

        return newRestaurant
    }

    override suspend fun getRestaurantByName(name: String): Restaurant? {
        return restaurantsFlow.value.find { it.name == name }
    }

    override suspend fun getRestaurantByNameAndCityId(name: String, cityId: Int): Restaurant? {
        return restaurantsFlow.value.find {
            it.name == name && it.cityId == cityId
        }
    }

    override suspend fun searchByNamePrefix(query: String): List<Restaurant> {
        return restaurantsFlow.value.filter {
            it.name.startsWith(prefix = query, ignoreCase = true)
        }
    }
}
