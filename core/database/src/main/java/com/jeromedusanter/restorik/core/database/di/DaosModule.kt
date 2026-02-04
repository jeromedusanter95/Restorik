package com.jeromedusanter.restorik.core.database.di

import com.jeromedusanter.restorik.core.database.RestorikDatabase
import com.jeromedusanter.restorik.core.database.dao.DishDao
import com.jeromedusanter.restorik.core.database.dao.MealDao
import com.jeromedusanter.restorik.core.database.dao.RecentSearchDao
import com.jeromedusanter.restorik.core.database.dao.RestaurantDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {

    @Provides
    fun providesMealDao(database: RestorikDatabase): MealDao = database.mealDao()

    @Provides
    fun providesRestaurantDao(database: RestorikDatabase): RestaurantDao = database.restaurantDao()

    @Provides
    fun providesRecentSearchDao(database: RestorikDatabase): RecentSearchDao = database.recentSearchDao()

    @Provides
    fun providesDishDao(database: RestorikDatabase): DishDao = database.dishDao()
}