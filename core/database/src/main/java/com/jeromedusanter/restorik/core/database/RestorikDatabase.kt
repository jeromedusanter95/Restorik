package com.jeromedusanter.restorik.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jeromedusanter.restorik.core.database.dao.MealDao
import com.jeromedusanter.restorik.core.database.dao.RestaurantDao
import com.jeromedusanter.restorik.core.database.model.MealEntity
import com.jeromedusanter.restorik.core.database.model.RestaurantEntity

@Database(
    entities = [
        MealEntity::class,
        RestaurantEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
internal abstract class RestorikDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun restaurantDao(): RestaurantDao
}
