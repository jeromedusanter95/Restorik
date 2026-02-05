package com.jeromedusanter.restorik.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jeromedusanter.restorik.core.database.model.RestaurantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantDao {
    @Query("SELECT * FROM restaurants")
    fun observeAll(): Flow<List<RestaurantEntity>>

    @Query("SELECT * FROM restaurants WHERE id = :id")
    fun observeById(id: Int): Flow<RestaurantEntity?>

    @Query("SELECT * FROM restaurants WHERE name = :name")
    fun observeByName(name: String): Flow<RestaurantEntity?>

    @Query("SELECT * FROM restaurants WHERE name = :name")
    suspend fun getByName(name: String): RestaurantEntity?

    @Query("SELECT * FROM restaurants WHERE name = :name AND city_id = :cityId")
    suspend fun getByNameAndCityId(name: String, cityId: Int): RestaurantEntity?

    @Query(
        """
        SELECT restaurants.* FROM restaurants
        INNER JOIN restaurants_fts ON restaurants.id = restaurants_fts.rowid
        WHERE restaurants_fts MATCH :query || '*'
        ORDER BY name ASC
        LIMIT 5
        """
    )
    suspend fun searchByNamePrefix(query: String): List<RestaurantEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: RestaurantEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(meal: RestaurantEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(meals: List<RestaurantEntity>)

    @Query(
        """
        SELECT restaurants.* FROM restaurants
        INNER JOIN restaurants_fts ON restaurants.id = restaurants_fts.rowid
        WHERE restaurants_fts MATCH :query || '*'
        ORDER BY name ASC
        """
    )
    suspend fun searchRestaurants(query: String): List<RestaurantEntity>
}