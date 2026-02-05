package com.jeromedusanter.restorik.feature.meal.detail

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.jeromedusanter.restorik.core.model.Dish

@Immutable
data class MealDetailUiState(
    val restaurantName: String,
    val cityName: String,
    val name: String,
    val dishList: List<Dish>,
    val priceAsString: String,
    val ratingOnFive: Float,
    val photoUriList: List<Uri>,
    val isSomeoneElsePaying: Boolean = false
) {
    companion object {
        val EMPTY = MealDetailUiState(
            name = "",
            restaurantName = "",
            cityName = "",
            dishList = emptyList(),
            priceAsString = "0.0",
            ratingOnFive = 0f,
            photoUriList = emptyList(),
        )
    }
}