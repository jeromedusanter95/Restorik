package com.jeromedusanter.restorik.core.data

import com.jeromedusanter.restorik.core.database.dao.CityDao
import com.jeromedusanter.restorik.core.database.model.CityEntity
import com.jeromedusanter.restorik.core.model.City
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class CityRepositoryImpl @Inject constructor(
    private val cityDao: CityDao,
    private val cityMapper: CityMapper
) : CityRepository {

    override fun observeAll(): Flow<List<City>> {
        return cityDao.observeAll().map { entityList ->
            entityList.map { cityMapper.mapEntityToDomain(cityEntity = it) }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getById(id: Int): City? = withContext(Dispatchers.IO) {
        return@withContext cityDao.getById(id = id)?.let { cityMapper.mapEntityToDomain(cityEntity = it) }
    }

    override suspend fun getByName(name: String): City? = withContext(Dispatchers.IO) {
        return@withContext cityDao.getByName(name = name)?.let { cityMapper.mapEntityToDomain(cityEntity = it) }
    }

    override suspend fun saveByNameAndGetLocal(name: String): City = withContext(Dispatchers.IO) {
        // Using id = 0 to let Room auto-generate the ID
        val cityEntity = CityEntity(id = 0, name = name)
        val insertedId = cityDao.upsert(city = cityEntity)
        return@withContext City(id = insertedId.toInt(), name = name)
    }

    override suspend fun searchByNamePrefix(query: String): List<City> = withContext(Dispatchers.IO) {
        return@withContext cityDao.searchByNamePrefix(query = query).map {
            cityMapper.mapEntityToDomain(cityEntity = it)
        }
    }
}
