package com.jeromedusanter.restorik.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jeromedusanter.restorik.core.database.dao.CityDao
import com.jeromedusanter.restorik.core.database.dao.DishDao
import com.jeromedusanter.restorik.core.database.dao.MealDao
import com.jeromedusanter.restorik.core.database.dao.RecentSearchDao
import com.jeromedusanter.restorik.core.database.dao.RestaurantDao
import com.jeromedusanter.restorik.core.database.model.CityEntity
import com.jeromedusanter.restorik.core.database.model.CityFts
import com.jeromedusanter.restorik.core.database.model.DishEntity
import com.jeromedusanter.restorik.core.database.model.DishFts
import com.jeromedusanter.restorik.core.database.model.MealEntity
import com.jeromedusanter.restorik.core.database.model.MealFts
import com.jeromedusanter.restorik.core.database.model.RecentSearchEntity
import com.jeromedusanter.restorik.core.database.model.RestaurantEntity
import com.jeromedusanter.restorik.core.database.model.RestaurantFts

@Database(
    entities = [
        MealEntity::class,
        DishEntity::class,
        RestaurantEntity::class,
        CityEntity::class,
        RecentSearchEntity::class,
        MealFts::class,
        DishFts::class,
        RestaurantFts::class,
        CityFts::class,
    ],
    version = 13,
    exportSchema = true,
)
@TypeConverters(
    Converters::class,
)
internal abstract class RestorikDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun dishDao(): DishDao
    abstract fun restaurantDao(): RestaurantDao
    abstract fun cityDao(): CityDao
    abstract fun recentSearchDao(): RecentSearchDao
}
