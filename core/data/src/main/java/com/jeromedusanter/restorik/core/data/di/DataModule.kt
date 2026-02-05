package com.jeromedusanter.restorik.core.data.di

import com.jeromedusanter.restorik.core.data.CityRepository
import com.jeromedusanter.restorik.core.data.CityRepositoryImpl
import com.jeromedusanter.restorik.core.data.MealRepository
import com.jeromedusanter.restorik.core.data.MealRepositoryImpl
import com.jeromedusanter.restorik.core.data.RestaurantRepository
import com.jeromedusanter.restorik.core.data.RestaurantRepositoryImpl
import com.jeromedusanter.restorik.core.data.SearchRepository
import com.jeromedusanter.restorik.core.data.SearchRepositoryImpl
import com.jeromedusanter.restorik.core.data.UserPreferencesRepository
import com.jeromedusanter.restorik.core.data.UserPreferencesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    internal abstract fun bindsMealRepository(repository: MealRepositoryImpl): MealRepository

    @Binds
    internal abstract fun bindsRestaurantRepository(repository: RestaurantRepositoryImpl): RestaurantRepository

    @Binds
    internal abstract fun bindsSearchRepository(repository: SearchRepositoryImpl): SearchRepository

    @Binds
    @Singleton
    abstract fun bindsUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl,
    ): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindsCityRepository(
        impl: CityRepositoryImpl,
    ): CityRepository
}
