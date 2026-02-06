package com.jeromedusanter.restorik.core.database.di

import android.content.Context
import androidx.room.Room
import com.jeromedusanter.restorik.core.database.RestorikDatabase
import com.jeromedusanter.restorik.core.database.util.PrepopulateCitiesCallback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun providesRestorikDatabase(
        @ApplicationContext context: Context,
    ): RestorikDatabase {
        return Room.databaseBuilder(
            context,
            RestorikDatabase::class.java,
            "restorik-database",
        )
            .addCallback(PrepopulateCitiesCallback())
            .build()
    }
}
