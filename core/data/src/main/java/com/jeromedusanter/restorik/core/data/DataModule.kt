package com.jeromedusanter.restorik.core.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun bindsMealRepository(repository: MealRepositoryImpl): MealRepository

    @Binds
    internal abstract fun bindsRestaurantRepository(repository: RestaurantRepositoryImpl): RestaurantRepository
}