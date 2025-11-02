package com.jeromedusanter.restorik.core.data

import com.jeromedusanter.restorik.core.model.Restaurant
import kotlinx.coroutines.flow.Flow

interface RestaurantRepository {
    fun observeById(id: Int): Flow<Restaurant>
    fun observeAll(): Flow<List<Restaurant>>
    suspend fun saveByNameAndGetLocal(restaurantName: String): Restaurant
    suspend fun getRestaurantByName(name: String): Restaurant?
}