package com.jeromedusanter.restorik.core.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jeromedusanter.restorik.core.database.RestorikDatabase
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
    fun providesNiaDatabase(
        @ApplicationContext context: Context,
    ): RestorikDatabase {
        return Room.databaseBuilder(
            context,
            RestorikDatabase::class.java,
            "restorik-database",
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
