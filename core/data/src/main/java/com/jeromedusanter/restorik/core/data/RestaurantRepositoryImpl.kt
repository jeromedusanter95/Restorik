package com.jeromedusanter.restorik.core.data

import com.jeromedusanter.restorik.core.database.dao.RestaurantDao
import com.jeromedusanter.restorik.core.database.model.RestaurantEntity
import com.jeromedusanter.restorik.core.model.Restaurant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RestaurantRepositoryImpl @Inject constructor(
    private val restaurantDao: RestaurantDao
) : RestaurantRepository {

    override fun observeById(id: Int) = restaurantDao.observeById(id).map { it.toModel() }

    override fun observeAll(): Flow<List<Restaurant>> {
        return restaurantDao.observeAll().map { entities ->
            entities.map { it.toModel() }
        }
    }

    override suspend fun saveByNameAndGetLocal(restaurantName: String, cityId: Int): Restaurant {
        // Validate input
        require(restaurantName.isNotBlank()) {
            "Restaurant name cannot be empty"
        }
        require(cityId > 0) {
            "City ID must be valid"
        }

        // Check if restaurant already exists in this city
        restaurantDao.getByNameAndCityId(name = restaurantName, cityId = cityId)?.let { return it.toModel() }

        // Insert new restaurant and get the generated ID
        // Using id = 0 to let Room auto-generate the ID
        val insertedId = restaurantDao.insert(
            RestaurantEntity(id = 0, name = restaurantName, cityId = cityId)
        )

        // If insert returns -1, the restaurant already exists (IGNORE conflict strategy)
        // This can happen due to race conditions in concurrent operations
        if (insertedId == -1L) {
            return restaurantDao.getByNameAndCityId(name = restaurantName, cityId = cityId)?.toModel()
                ?: throw IllegalStateException("Restaurant '$restaurantName' in city $cityId should exist but was not found")
        }

        // Validate the inserted ID is valid
        require(insertedId > 0) {
            "Failed to insert restaurant: invalid ID returned"
        }

        // Return newly created restaurant with the inserted ID
        return Restaurant(id = insertedId.toInt(), name = restaurantName, cityId = cityId)
    }

    override suspend fun getRestaurantByName(name: String): Restaurant? {
        return restaurantDao.getByName(name)?.toModel()
    }

    override suspend fun getRestaurantByNameAndCityId(name: String, cityId: Int): Restaurant? {
        return restaurantDao.getByNameAndCityId(name = name, cityId = cityId)?.toModel()
    }

    override suspend fun searchByNamePrefix(query: String): List<Restaurant> {
        if (query.isBlank()) return emptyList()
        return restaurantDao.searchByNamePrefix(query = query).map { it.toModel() }
    }
}

private fun RestaurantEntity?.toModel(): Restaurant {
    return Restaurant(
        id = this?.id ?: -1,
        name = this?.name ?: "",
        cityId = this?.cityId ?: -1
    )
}
