package com.jeromedusanter.restorik.feature.meal.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme
import com.jeromedusanter.restorik.feature.meal.MealViewModel

@Composable
fun MealDetailScreen(
    modifier: Modifier = Modifier,
    mealId: Int?,
    mealListViewModel: MealViewModel = hiltViewModel()
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Meal Detail Screen !")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MealDetailScreenPreview() {
    RestorikTheme {
        MealDetailScreen(mealId = 1)
    }
}