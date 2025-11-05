package com.jeromedusanter.restorik.feature.meal.list

import android.net.Uri
import androidx.compose.runtime.Immutable

@Immutable
data class MealUiModel(
    val id: Int,
    val name: String,
    val restaurantName: String,
    val date: String,
    val rating: Int,
    val photoUri: Uri?,
)