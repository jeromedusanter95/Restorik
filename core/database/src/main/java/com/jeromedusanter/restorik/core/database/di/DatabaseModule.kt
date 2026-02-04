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
        val migration3To4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create a new temporary table with the new schema
                db.execSQL("""
                    CREATE TABLE dishes_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        meal_id INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        rating REAL NOT NULL,
                        description TEXT NOT NULL,
                        price REAL NOT NULL,
                        dish_type TEXT NOT NULL,
                        FOREIGN KEY(meal_id) REFERENCES meals(id) ON DELETE CASCADE
                    )
                """.trimIndent())

                // Copy data from old table to new table, converting rating to float
                db.execSQL("""
                    INSERT INTO dishes_new (id, meal_id, name, rating, description, price, dish_type)
                    SELECT id, meal_id, name, CAST(rating AS REAL), description, price, dish_type
                    FROM dishes
                """.trimIndent())

                // Drop the old table
                db.execSQL("DROP TABLE dishes")

                // Rename the new table to the original name
                db.execSQL("ALTER TABLE dishes_new RENAME TO dishes")

                // Recreate the index
                db.execSQL("CREATE INDEX IF NOT EXISTS index_dishes_meal_id ON dishes(meal_id)")
            }
        }

        return Room.databaseBuilder(
            context,
            RestorikDatabase::class.java,
            "restorik-database",
        )
            .addMigrations(migration3To4)
            .fallbackToDestructiveMigration()
            .build()
    }
}
