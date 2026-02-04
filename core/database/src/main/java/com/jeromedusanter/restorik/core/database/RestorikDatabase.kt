package com.jeromedusanter.restorik.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jeromedusanter.restorik.core.database.dao.DishDao
import com.jeromedusanter.restorik.core.database.dao.MealDao
import com.jeromedusanter.restorik.core.database.dao.RecentSearchDao
import com.jeromedusanter.restorik.core.database.dao.RestaurantDao
import com.jeromedusanter.restorik.core.database.model.DishEntity
import com.jeromedusanter.restorik.core.database.model.MealEntity
import com.jeromedusanter.restorik.core.database.model.RecentSearchEntity
import com.jeromedusanter.restorik.core.database.model.RestaurantEntity

@Database(
    entities = [
        MealEntity::class,
        DishEntity::class,
        RestaurantEntity::class,
        RecentSearchEntity::class,
    ],
    version = 5,
    exportSchema = true,
)
@TypeConverters(
    Converters::class,
)
internal abstract class RestorikDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun dishDao(): DishDao
    abstract fun restaurantDao(): RestaurantDao
    abstract fun recentSearchDao(): RecentSearchDao
}
