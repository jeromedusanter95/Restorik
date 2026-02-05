package com.jeromedusanter.restorik.core.data

import com.jeromedusanter.restorik.core.model.Restaurant
import kotlinx.coroutines.flow.Flow

interface RestaurantRepository {
    suspend fun getById(id: Int): Restaurant?
    fun observeById(id: Int): Flow<Restaurant>
    fun observeAll(): Flow<List<Restaurant>>
    suspend fun saveByNameAndGetLocal(restaurantName: String, cityId: Int): Restaurant
    suspend fun getRestaurantByName(name: String): Restaurant?
    suspend fun getRestaurantByNameAndCityId(name: String, cityId: Int): Restaurant?
    suspend fun searchByNamePrefix(query: String): List<Restaurant>
}