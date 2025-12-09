package com.jeromedusanter.restorik.feature.meal.detail

import androidx.core.net.toUri
import com.jeromedusanter.restorik.core.model.Meal
import javax.inject.Inject

class MealDetailMapper @Inject constructor() {

    fun mapToUiModel(meal: Meal, restaurantName: String): MealDetailUiState {
        return MealDetailUiState(
            restaurantName = restaurantName,
            name = meal.name,
            comment = meal.comment,
            priceAsString = meal.price.toString(),
            ratingOnFive = meal.ratingOnFive,
            photoUriList = meal.photoList.map(String::toUri)
        )
    }
}