package com.jeromedusanter.restorik.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "meals",
    indices = [
        Index(value = ["date_time"]), // Optimizes: searchMeals(), searchMealsWithDishes() (ORDER BY)
        Index(value = ["restaurant_id"]), // Optimizes: observeByRestaurantId(), observeByRestaurantIdWithDishes() (WHERE)
        Index(value = ["restaurant_id", "date_time"]) // Optimizes: observeByRestaurantId(), observeByRestaurantIdWithDishes() (WHERE + ORDER BY)
    ]
)
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "restaurant_id") var restaurantId: Int,
    @ColumnInfo(name = "date_time") val dateTime: String,
    @ColumnInfo(name = "picture_path_list") val photoList: List<String>,
    @ColumnInfo(name = "is_someone_else_paying") val isSomeoneElsePaying: Boolean
)