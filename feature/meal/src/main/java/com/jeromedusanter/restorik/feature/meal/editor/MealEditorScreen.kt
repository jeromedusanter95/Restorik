package com.jeromedusanter.restorik.feature.meal.editor

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme
import com.jeromedusanter.restorik.core.ui.AddPhotoButton
import com.jeromedusanter.restorik.core.ui.PhotoViewDialog
import com.jeromedusanter.restorik.core.ui.RestorikOutlineTextField
import com.jeromedusanter.restorik.core.ui.RestorikRatingBar
import com.jeromedusanter.restorik.feature.meal.R

@Composable
fun MealEditorScreen(
    uiState: MealEditorUiState,
    onNameChange: (String) -> Unit,
    onRestaurantNameChange: (String) -> Unit,
    onCommentChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onRatingChange: (Int) -> Unit,
    onSelectRestaurantSuggestion: (RestaurantSuggestion) -> Unit,
    onClearFieldError: (MealEditorField) -> Unit,
    onDeletePhoto: (Uri) -> Unit,
    onShowPhotoSelectionBottomSheet: () -> Unit,
    onSelectPhotoForView: (Uri) -> Unit,
    onClearSelectedPhoto: () -> Unit,
    onSaveMeal: () -> Unit,
    onMoveFocusDown: () -> Unit,
    onClearFocus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.SpaceAround,
    ) {
        RestorikOutlineTextField(
            modifier = modifier.fillMaxWidth(),
            value = uiState.name,
            onValueChange = { newValue ->
                onNameChange(newValue)
                onClearFieldError(MealEditorField.MEAL_NAME)
            },
            label = stringResource(R.string.feature_meal_meal_name_label),
            enabled = !uiState.isLoading,
            isRequired = true,
            isError = uiState.fieldErrors.mealNameError != null,
            supportingText = uiState.fieldErrors.mealNameError,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { onMoveFocusDown() }
            )
        )

        Column(modifier = modifier.fillMaxWidth()) {
            RestorikOutlineTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.restaurantName,
                onValueChange = { newValue ->
                    onRestaurantNameChange(newValue)
                    onClearFieldError(MealEditorField.RESTAURANT_NAME)
                },
                label = stringResource(R.string.feature_meal_restaurant_name_label),
                enabled = !uiState.isLoading,
                isRequired = true,
                isError = uiState.fieldErrors.restaurantNameError != null,
                supportingText = uiState.fieldErrors.restaurantNameError,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { onMoveFocusDown() }
                )
            )

            // Restaurant suggestions dropdown
            if (uiState.restaurantSuggestionList.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column {
                        uiState.restaurantSuggestionList.forEachIndexed { index, restaurant ->
                            ListItem(
                                headlineContent = { Text(text = restaurant.name) },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Default.Restaurant,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.clickable {
                                    onSelectRestaurantSuggestion(restaurant)
                                }
                            )
                            if (index < uiState.restaurantSuggestionList.size - 1) {
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }

        RestorikOutlineTextField(
            modifier = modifier.fillMaxWidth(),
            value = uiState.comment,
            onValueChange = onCommentChange,
            singleLine = false,
            label = stringResource(R.string.feature_meal_comment_label),
            enabled = !uiState.isLoading,
        )

        RestorikOutlineTextField(
            modifier = modifier.fillMaxWidth(),
            value = uiState.priceAsString,
            onValueChange = { newValue ->
                onPriceChange(newValue)
                onClearFieldError(MealEditorField.PRICE)
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Euro,
                    contentDescription = stringResource(R.string.feature_meal_euro_content_description)
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onClearFocus() }
            ),
            label = stringResource(R.string.feature_meal_price_label),
            enabled = !uiState.isLoading,
            isRequired = true,
            isError = uiState.fieldErrors.priceError != null,
            supportingText = uiState.fieldErrors.priceError
        )

        Column(modifier = modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.feature_meal_rating_label),
                color = if (uiState.fieldErrors.ratingError != null) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            RestorikRatingBar(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                value = uiState.ratingOnFive,
                onValueChanged = { rating ->
                    onRatingChange(rating)
                    onClearFieldError(MealEditorField.RATING)
                }
            )
            uiState.fieldErrors.ratingError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        if (uiState.showAddButtonPhoto) {
            AddPhotoButton(
                onClick = onShowPhotoSelectionBottomSheet
            )
        } else {
            Text("${stringResource(R.string.feature_meal_photos_label)} ${uiState.photoTitleSuffix}")
            HorizontalPhotoList(
                photoUriList = uiState.photoUriList,
                showAddPhotoItem = uiState.showAddButtonPhotoItem,
                onClickDelete = onDeletePhoto,
                onClickAdd = onShowPhotoSelectionBottomSheet,
                onClickItem = onSelectPhotoForView
            )
            uiState.selectedPhotoUri?.let { photoUri ->
                PhotoViewDialog(
                    photoUri = photoUri,
                    onDismiss = onClearSelectedPhoto
                )
            }
        }

        Button(
            onClick = onSaveMeal,
            modifier = modifier.align(alignment = Alignment.CenterHorizontally),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Text(stringResource(R.string.feature_meal_save_button))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MealEditorPreview() {
    RestorikTheme {
        MealEditorScreen(
            uiState = MealEditorUiState(
                id = 0,
                restaurantName = "Le Bistrot Parisien",
                name = "Steak Frites",
                comment = "Excellent steak with crispy fries",
                priceAsString = "24.50",
                ratingOnFive = 4,
                photoUriList = emptyList(),
                isLoading = false,
                showAddButtonPhoto = true,
                showAddButtonPhotoItem = false,
                photoTitleSuffix = "",
                showPhotoSelectionBottomSheet = false,
                selectedPhotoUri = null,
                errorMessage = null,
                fieldErrors = FieldErrors(),
                restaurantSuggestionList = listOf(
                    RestaurantSuggestion(id = 1, name = "Le Bistrot Parisien"),
                    RestaurantSuggestion(id = 2, name = "Le Bistrot de la Gare")
                )
            ),
            onNameChange = {},
            onRestaurantNameChange = {},
            onCommentChange = {},
            onPriceChange = {},
            onRatingChange = {},
            onSelectRestaurantSuggestion = {},
            onClearFieldError = {},
            onDeletePhoto = {},
            onShowPhotoSelectionBottomSheet = {},
            onSelectPhotoForView = {},
            onClearSelectedPhoto = {},
            onSaveMeal = {},
            onMoveFocusDown = {},
            onClearFocus = {}
        )
    }
}