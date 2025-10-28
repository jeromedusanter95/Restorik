package com.jeromedusanter.restorik.feature.meal.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme

@Composable
fun MealEditorScreen(
    modifier: Modifier = Modifier,
    viewModel: MealEditorViewModel = hiltViewModel()
) {

    val uiState = viewModel.uiState.collectAsState()

    Surface(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text("Nom du restaurant")
            TextField(
                value = uiState.value.restaurantName,
                onValueChange = viewModel::updateRestaurantName
            )

            Text("Nom du repas")
            TextField(
                value = uiState.value.name,
                onValueChange = viewModel::updateMealName
            )

            Text("Commentaire")
            TextField(
                value = uiState.value.comment,
                onValueChange = viewModel::updateMealComment
            )

            Text("Prix")
            TextField(
                value = uiState.value.priceAsString,
                onValueChange = viewModel::updateRestaurantName
            )

            Text("Note")
            TextField(
                value = uiState.value.ratingOnFiveAsStars,
                onValueChange = viewModel::updateRatingOnFive
            )

            Text("Add photo")
            TextField(
                value = uiState.value.restaurantName,
                onValueChange = viewModel::updateRestaurantName
            )

            Button(
                onClick = viewModel::saveMeal,
                modifier = modifier.align(alignment = Alignment.CenterHorizontally)
            ) {
                Text("Enregistrer")
            }
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