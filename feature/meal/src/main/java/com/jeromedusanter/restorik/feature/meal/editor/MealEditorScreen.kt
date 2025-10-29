package com.jeromedusanter.restorik.feature.meal.editor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jeromedusanter.restorik.core.camera.CapturePhotoContract
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme
import com.jeromedusanter.restorik.core.ui.AddPhotoButton
import com.jeromedusanter.restorik.core.ui.RestorikOutlineTextField
import com.jeromedusanter.restorik.core.ui.RestorikRatingBar

@Composable
fun MealEditorScreen(
    modifier: Modifier = Modifier,
    viewModel: MealEditorViewModel = hiltViewModel()
) {

    val uiState = viewModel.uiState.collectAsState()

    val captureLauncher = rememberLauncherForActivityResult(CapturePhotoContract()) { uri ->
        if (uri != null) {
            viewModel.onPhotoCaptured(uri)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            RestorikOutlineTextField(
                modifier = modifier.fillMaxWidth(),
                value = uiState.value.name,
                onValueChange = viewModel::updateMealName,
                label = "Nom du repas"
            )

            RestorikOutlineTextField(
                modifier = modifier.fillMaxWidth(),
                value = uiState.value.restaurantName,
                onValueChange = viewModel::updateRestaurantName,
                label = "Nom du restaurant"
            )

            RestorikOutlineTextField(
                modifier = modifier.fillMaxWidth(),
                value = uiState.value.comment,
                onValueChange = viewModel::updateMealComment,
                singleLine = false,
                label = "Commentaire"
            )

            RestorikOutlineTextField(
                modifier = modifier.fillMaxWidth(),
                value = uiState.value.priceAsString,
                onValueChange = viewModel::updatePrice,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Euro,
                        contentDescription = "Euro"
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                label = "Prix"
            )

            Text("Note")
            RestorikRatingBar(
                modifier = modifier.align(alignment = Alignment.CenterHorizontally),
                value = uiState.value.ratingOnFive,
                onValueChanged = viewModel::updateRating
            )

            AddPhotoButton(onClick = { captureLauncher.launch(Unit) })

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