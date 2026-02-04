package com.jeromedusanter.restorik.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jeromedusanter.restorik.core.database.model.MealEntity
import com.jeromedusanter.restorik.core.database.model.MealWithDishes
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    @Query("SELECT * FROM meals")
    fun observeAll(): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE id = :id")
    fun observerById(id: Int): Flow<MealEntity?>

    @Transaction
    @Query("SELECT * FROM meals")
    fun observeAllWithDishes(): Flow<List<MealWithDishes>>

    @Transaction
    @Query("SELECT * FROM meals WHERE id = :id")
    fun observeByIdWithDishes(id: Int): Flow<MealWithDishes?>

    @Transaction
    @Query("SELECT * FROM meals WHERE restaurant_id = :restaurantId ORDER BY date_time DESC")
    fun observeByRestaurantIdWithDishes(restaurantId: Int): Flow<List<MealWithDishes>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(meal: MealEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(meals: List<MealEntity>)

    @Query("DELETE FROM meals WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query(
        """
        SELECT DISTINCT meals.* FROM meals
        LEFT JOIN restaurants ON meals.restaurant_id = restaurants.id
        LEFT JOIN dishes ON meals.id = dishes.meal_id
        WHERE meals.name LIKE :query || '%'
        OR meals.name LIKE '% ' || :query || '%'
        OR restaurants.name LIKE :query || '%'
        OR restaurants.name LIKE '% ' || :query || '%'
        OR dishes.name LIKE :query || '%'
        OR dishes.name LIKE '% ' || :query || '%'
        OR dishes.description LIKE :query || '%'
        OR dishes.description LIKE '% ' || :query || '%'
        ORDER BY meals.date_time DESC
        """
    )
    suspend fun searchMeals(query: String): List<MealEntity>

    @Transaction
    @Query(
        """
        SELECT DISTINCT meals.* FROM meals
        LEFT JOIN restaurants ON meals.restaurant_id = restaurants.id
        LEFT JOIN dishes ON meals.id = dishes.meal_id
        WHERE meals.name LIKE :query || '%'
        OR meals.name LIKE '% ' || :query || '%'
        OR restaurants.name LIKE :query || '%'
        OR restaurants.name LIKE '% ' || :query || '%'
        OR dishes.name LIKE :query || '%'
        OR dishes.name LIKE '% ' || :query || '%'
        OR dishes.description LIKE :query || '%'
        OR dishes.description LIKE '% ' || :query || '%'
        ORDER BY meals.date_time DESC
        """
    )
    suspend fun searchMealsWithDishes(query: String): List<MealWithDishes>

    @Query("SELECT * FROM meals WHERE restaurant_id = :restaurantId ORDER BY date_time DESC")
    fun observeByRestaurantId(restaurantId: Int): Flow<List<MealEntity>>
}