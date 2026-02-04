package com.jeromedusanter.restorik.feature.meal.detail

import androidx.core.net.toUri
import com.jeromedusanter.restorik.core.model.Meal
import java.util.Locale
import javax.inject.Inject

class MealDetailMapper @Inject constructor() {

    fun mapToUiModel(meal: Meal, restaurantName: String): MealDetailUiState {
        return MealDetailUiState(
            restaurantName = restaurantName,
            name = meal.name,
            dishList = meal.dishList,
            priceAsString = String.format(Locale.getDefault(), "%.2f", meal.price),
            ratingOnFive = meal.ratingOnFive,
            photoUriList = meal.photoList.map(String::toUri)
        )
    }
}