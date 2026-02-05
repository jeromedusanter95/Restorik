package com.jeromedusanter.restorik.core.data

import com.jeromedusanter.restorik.core.model.City
import kotlinx.coroutines.flow.Flow

interface CityRepository {
    fun observeAll(): Flow<List<City>>
    suspend fun getById(id: Int): City?
    suspend fun getByName(name: String): City?
    suspend fun saveByNameAndGetLocal(name: String): City
    suspend fun searchByNamePrefix(query: String): List<City>
}
