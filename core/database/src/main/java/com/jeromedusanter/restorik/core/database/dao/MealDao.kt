package com.jeromedusanter.restorik.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jeromedusanter.restorik.core.database.model.MealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    @Query("SELECT * FROM meals")
    fun observeAll(): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE id = :id")
    fun observerById(id: Int): Flow<MealEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(meal: MealEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(meals: List<MealEntity>)

    @Query("DELETE FROM meals WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query(
        """
        SELECT meals.* FROM meals
        LEFT JOIN restaurants ON meals.restaurant_id = restaurants.id
        WHERE meals.name LIKE :query || '%'
        OR meals.name LIKE '% ' || :query || '%'
        OR meals.comment LIKE :query || '%'
        OR meals.comment LIKE '% ' || :query || '%'
        OR restaurants.name LIKE :query || '%'
        OR restaurants.name LIKE '% ' || :query || '%'
        ORDER BY meals.date_time DESC
        """
    )
    suspend fun searchMeals(query: String): List<MealEntity>

    @Query("SELECT * FROM meals WHERE restaurant_id = :restaurantId ORDER BY date_time DESC")
    fun observeByRestaurantId(restaurantId: Int): Flow<List<MealEntity>>
}