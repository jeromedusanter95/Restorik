package com.jeromedusanter.restorik.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jeromedusanter.restorik.core.database.model.DishEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DishDao {
    @Query("SELECT * FROM dishes WHERE meal_id = :mealId")
    fun observeByMealId(mealId: Int): Flow<List<DishEntity>>

    @Query("SELECT * FROM dishes WHERE id = :id")
    fun observeById(id: Int): Flow<DishEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(dish: DishEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(dishList: List<DishEntity>)

    @Query("DELETE FROM dishes WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM dishes WHERE meal_id = :mealId")
    suspend fun deleteByMealId(mealId: Int)
}
