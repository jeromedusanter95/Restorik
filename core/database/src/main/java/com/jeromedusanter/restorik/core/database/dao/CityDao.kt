package com.jeromedusanter.restorik.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jeromedusanter.restorik.core.database.model.CityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Query("SELECT * FROM cities ORDER BY name ASC")
    fun observeAll(): Flow<List<CityEntity>>

    @Query("SELECT * FROM cities WHERE id = :id")
    suspend fun getById(id: Int): CityEntity?

    @Query("SELECT * FROM cities WHERE name = :name")
    suspend fun getByName(name: String): CityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(city: CityEntity): Long

    @Query("SELECT * FROM cities WHERE name LIKE :query || '%' ORDER BY name ASC LIMIT 10")
    suspend fun searchByNamePrefix(query: String): List<CityEntity>
}
