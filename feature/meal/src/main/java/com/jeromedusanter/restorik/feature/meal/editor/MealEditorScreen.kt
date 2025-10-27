package com.jeromedusanter.restorik.feature.meal.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme
import com.jeromedusanter.restorik.feature.meal.MealViewModel

@Composable
fun MealEditorScreen(
    modifier: Modifier = Modifier,
    mealListViewModel: MealViewModel = hiltViewModel()
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Meal Editor Screen !")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MealEditorScreenPreview() {
    RestorikTheme {
        MealEditorScreen()
    }
}