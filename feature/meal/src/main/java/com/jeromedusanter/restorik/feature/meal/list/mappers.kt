package com.jeromedusanter.restorik.feature.meal.list

import androidx.core.net.toUri
import com.jeromedusanter.restorik.core.common.resources.ResourceProvider
import com.jeromedusanter.restorik.core.model.GroupDate
import com.jeromedusanter.restorik.core.model.Meal
import com.jeromedusanter.restorik.feature.meal.R
import java.time.format.DateTimeFormatter

fun GroupDate.toLocalizedString(resourceProvider: ResourceProvider): String {
    return when (this) {
        GroupDate.TODAY -> resourceProvider.getString(R.string.feature_meal_date_category_today)
        GroupDate.YESTERDAY -> resourceProvider.getString(R.string.feature_meal_date_category_yesterday)
        GroupDate.WEEK -> resourceProvider.getString(R.string.feature_meal_date_category_this_week)
        GroupDate.MONTH -> resourceProvider.getString(R.string.feature_meal_date_category_this_month)
        GroupDate.OLDER -> resourceProvider.getString(R.string.feature_meal_date_category_older)
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