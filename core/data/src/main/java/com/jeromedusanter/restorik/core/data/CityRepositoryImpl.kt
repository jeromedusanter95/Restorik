package com.jeromedusanter.restorik.core.data

import com.jeromedusanter.restorik.core.database.dao.CityDao
import com.jeromedusanter.restorik.core.database.model.CityEntity
import com.jeromedusanter.restorik.core.model.City
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class CityRepositoryImpl @Inject constructor(
    private val cityDao: CityDao,
    private val cityMapper: CityMapper
) : CityRepository {

    override fun observeAll(): Flow<List<City>> {
        return cityDao.observeAll().map { entityList ->
            entityList.map { cityMapper.mapEntityToDomain(cityEntity = it) }
        }
    }

    override suspend fun getById(id: Int): City? {
        return cityDao.getById(id = id)?.let { cityMapper.mapEntityToDomain(cityEntity = it) }
    }

    override suspend fun getByName(name: String): City? {
        return cityDao.getByName(name = name)?.let { cityMapper.mapEntityToDomain(cityEntity = it) }
    }

    override suspend fun saveByNameAndGetLocal(name: String): City {
        // Using id = 0 to let Room auto-generate the ID
        val cityEntity = CityEntity(id = 0, name = name)
        val insertedId = cityDao.upsert(city = cityEntity)
        return City(id = insertedId.toInt(), name = name)
    }

    override suspend fun searchByNamePrefix(query: String): List<City> {
        return cityDao.searchByNamePrefix(query = query).map {
            cityMapper.mapEntityToDomain(cityEntity = it)
        }
    }
}
