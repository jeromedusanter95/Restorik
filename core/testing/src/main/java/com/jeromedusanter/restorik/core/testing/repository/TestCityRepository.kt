package com.jeromedusanter.restorik.core.testing.repository

import com.jeromedusanter.restorik.core.data.CityRepository
import com.jeromedusanter.restorik.core.model.City
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TestCityRepository : CityRepository {

    /**
     * Internal storage for cities
     */
    private val citiesFlow = MutableStateFlow<List<City>>(emptyList())
    private var nextId = 1

    /**
     * Send test data to the repository
     * All Flows will automatically update based on this data
     */
    fun sendCities(cities: List<City>) {
        citiesFlow.value = cities
        // Update nextId to avoid conflicts
        nextId = (cities.maxOfOrNull { it.id } ?: 0) + 1
    }

    override fun observeAll(): Flow<List<City>> {
        return citiesFlow
    }

    override suspend fun getById(id: Int): City? {
        return citiesFlow.value.find { it.id == id }
    }

    override suspend fun getByName(name: String): City? {
        return citiesFlow.value.find { it.name == name }
    }

    override suspend fun saveByNameAndGetLocal(name: String): City {
        // Check if already exists
        val existing = citiesFlow.value.find { it.name == name }

        if (existing != null) {
            return existing
        }

        // Create new city
        val newCity = City(
            id = nextId++,
            name = name
        )

        val currentCities = citiesFlow.value.toMutableList()
        currentCities.add(newCity)
        citiesFlow.value = currentCities

        return newCity
    }

    override suspend fun searchByNamePrefix(query: String): List<City> {
        return citiesFlow.value.filter {
            it.name.startsWith(prefix = query, ignoreCase = true)
        }
    }
}
