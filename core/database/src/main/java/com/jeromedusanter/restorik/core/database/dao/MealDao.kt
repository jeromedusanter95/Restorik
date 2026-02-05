package com.jeromedusanter.restorik.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jeromedusanter.restorik.core.database.model.MealEntity
import com.jeromedusanter.restorik.core.database.model.MealWithDishes
import com.jeromedusanter.restorik.core.database.model.RestaurantSpendingResult
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
        INNER JOIN restaurants ON meals.restaurant_id = restaurants.id
        WHERE meals.id IN (
            SELECT meals_fts.rowid FROM meals_fts WHERE meals_fts MATCH :query || '*'
        )
        OR meals.restaurant_id IN (
            SELECT restaurants_fts.rowid FROM restaurants_fts WHERE restaurants_fts MATCH :query || '*'
        )
        OR restaurants.city_id IN (
            SELECT cities_fts.rowid FROM cities_fts WHERE cities_fts MATCH :query || '*'
        )
        OR meals.id IN (
            SELECT dishes.meal_id FROM dishes
            INNER JOIN dishes_fts ON dishes.id = dishes_fts.rowid
            WHERE dishes_fts MATCH :query || '*'
        )
        ORDER BY meals.date_time DESC
        """
    )
    suspend fun searchMeals(query: String): List<MealEntity>

    @Transaction
    @Query(
        """
        SELECT DISTINCT meals.* FROM meals
        INNER JOIN restaurants ON meals.restaurant_id = restaurants.id
        WHERE meals.id IN (
            SELECT meals_fts.rowid FROM meals_fts WHERE meals_fts MATCH :query || '*'
        )
        OR meals.restaurant_id IN (
            SELECT restaurants_fts.rowid FROM restaurants_fts WHERE restaurants_fts MATCH :query || '*'
        )
        OR restaurants.city_id IN (
            SELECT cities_fts.rowid FROM cities_fts WHERE cities_fts MATCH :query || '*'
        )
        OR meals.id IN (
            SELECT dishes.meal_id FROM dishes
            INNER JOIN dishes_fts ON dishes.id = dishes_fts.rowid
            WHERE dishes_fts MATCH :query || '*'
        )
        ORDER BY meals.date_time DESC
        """
    )
    suspend fun searchMealsWithDishes(query: String): List<MealWithDishes>

    @Query("SELECT * FROM meals WHERE restaurant_id = :restaurantId ORDER BY date_time DESC")
    fun observeByRestaurantId(restaurantId: Int): Flow<List<MealEntity>>

    @Query("""
        SELECT COALESCE(SUM(dishes.price), 0.0)
        FROM meals
        JOIN dishes ON meals.id = dishes.meal_id
        WHERE meals.date_time LIKE :yearMonth || '%'
    """)
    fun getTotalSpendingForMonth(yearMonth: String): Flow<Double>

    @Query("""
        SELECT COUNT(*)
        FROM meals
        WHERE meals.date_time LIKE :yearMonth || '%'
    """)
    fun getMealCountForMonth(yearMonth: String): Flow<Int>

    @Query("""
        SELECT COUNT(DISTINCT restaurant_id)
        FROM meals
        WHERE meals.date_time LIKE :yearMonth || '%'
    """)
    fun getUniqueRestaurantCountForMonth(yearMonth: String): Flow<Int>

    @Query("""
        SELECT COALESCE(AVG(dishes.rating), 0.0)
        FROM meals
        JOIN dishes ON meals.id = dishes.meal_id
        WHERE meals.date_time LIKE :yearMonth || '%'
    """)
    fun getAverageRatingForMonth(yearMonth: String): Flow<Double>

    @Query("SELECT MIN(date_time) FROM meals")
    fun getFirstMealDate(): Flow<String?>

    @Query("""
        SELECT meals.restaurant_id, COALESCE(SUM(dishes.price), 0.0) as total_spending
        FROM meals
        JOIN dishes ON meals.id = dishes.meal_id
        WHERE meals.date_time LIKE :yearMonth || '%'
        GROUP BY meals.restaurant_id
        ORDER BY total_spending DESC
        LIMIT :limit
    """)
    fun getTopRestaurantsBySpendingForMonth(yearMonth: String, limit: Int): Flow<List<RestaurantSpendingResult>>

    @Query("""
        SELECT DISTINCT restaurant_id
        FROM meals
        WHERE meals.date_time LIKE :yearMonth || '%'
    """)
    suspend fun getUniqueRestaurantIdsForMonth(yearMonth: String): List<Int>

    @Query("""
        SELECT DISTINCT restaurant_id
        FROM meals
        WHERE meals.date_time < :yearMonth
    """)
    suspend fun getUniqueRestaurantIdsBeforeMonth(yearMonth: String): List<Int>
}