package com.jeromedusanter.restorik.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "comment") val comment: String,
    @ColumnInfo(name = "price") val price: Double,
    @ColumnInfo(name = "restaurant_id") var restaurantId: Int,
    @ColumnInfo(name = "date_time") val dateTime: String,
    @ColumnInfo(name = "rating_on_five") val ratingOnFive: Int,
    @ColumnInfo(name = "picture_path_list") val photoList: List<String>
)