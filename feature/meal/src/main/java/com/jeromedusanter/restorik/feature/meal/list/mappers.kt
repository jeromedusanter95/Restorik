package com.jeromedusanter.restorik.feature.meal.list

import android.content.Context
import androidx.core.net.toUri
import com.jeromedusanter.restorik.core.model.GroupDate
import com.jeromedusanter.restorik.core.model.Meal
import com.jeromedusanter.restorik.feature.meal.R
import java.time.format.DateTimeFormatter

fun GroupDate.toLocalizedString(context: Context): String {
    return when (this) {
        GroupDate.TODAY -> context.getString(R.string.feature_meal_date_category_today)
        GroupDate.YESTERDAY -> context.getString(R.string.feature_meal_date_category_yesterday)
        GroupDate.WEEK -> context.getString(R.string.feature_meal_date_category_this_week)
        GroupDate.MONTH -> context.getString(R.string.feature_meal_date_category_this_month)
        GroupDate.OLDER -> context.getString(R.string.feature_meal_date_category_older)
    }
}

fun Meal.toUiModel(restaurantName: String, cityName: String): MealUiModel {
    return MealUiModel(
        id = id,
        name = name,
        restaurantName = restaurantName,
        cityName = cityName,
        date = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
        rating = ratingOnFive,
        photoUri = photoList.firstOrNull()?.toUri()
    )
}