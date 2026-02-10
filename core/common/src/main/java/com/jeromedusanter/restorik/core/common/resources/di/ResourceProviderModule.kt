package com.jeromedusanter.restorik.core.common.resources.di

import com.jeromedusanter.restorik.core.common.resources.AndroidResourceProvider
import com.jeromedusanter.restorik.core.common.resources.ResourceProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ResourceProviderModule {

    @Binds
    @Singleton
    abstract fun bindResourceProvider(
        androidResourceProvider: AndroidResourceProvider
    ): ResourceProvider
}
