package com.jeromedusanter.restorik.feature.meal.editor

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme
import com.jeromedusanter.restorik.core.model.Dish
import com.jeromedusanter.restorik.core.ui.PhotoViewDialog
import com.jeromedusanter.restorik.core.ui.RestorikOutlineTextField
import com.jeromedusanter.restorik.feature.meal.R

@Composable
fun MealEditorScreen(
    uiState: MealEditorUiState,
    onNameChange: (String) -> Unit,
    onRestaurantNameChange: (String) -> Unit,
    onSelectRestaurantSuggestion: (RestaurantSuggestion) -> Unit,
    onClearFieldError: (MealEditorField) -> Unit,
    onDeletePhoto: (Uri) -> Unit,
    onShowPhotoSelectionBottomSheet: () -> Unit,
    onSelectPhotoForView: (Uri) -> Unit,
    onClearSelectedPhoto: () -> Unit,
    onSaveMeal: () -> Unit,
    onMoveFocusDown: () -> Unit,
    onDeleteDish: (Int) -> Unit,
    onShowDishDialog: (Dish?) -> Unit,
    onDismissDishDialog: () -> Unit,
    onDishNameChange: (String) -> Unit,
    onDishDescriptionChange: (String) -> Unit,
    onDishPriceChange: (String) -> Unit,
    onDishRatingChange: (Float) -> Unit,
    onDishTypeChange: (com.jeromedusanter.restorik.core.model.DishType) -> Unit,
    onDishTypeExpandedChange: (Boolean) -> Unit,
    onSaveDish: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
        ,
        verticalArrangement = Arrangement.spacedBy(space = 16.dp)
    ) {
        // Meal section
        Column(modifier = modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.feature_meal_meal_info_label),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            RestorikOutlineTextField(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
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
        }

        // Restaurant section
        Column(modifier = modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.feature_meal_restaurant_info_label),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Column(modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp)) {
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
        }

        // Dishes section
        Column(modifier = modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.feature_meal_dishes_label),
                style = MaterialTheme.typography.titleMedium,
                color = if (uiState.fieldErrors.dishListError != null) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            uiState.fieldErrors.dishListError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(space = 8.dp)
            ) {
                uiState.dishList.forEach { dish ->
                    DishListItem(
                        dish = dish,
                        onEdit = { onShowDishDialog(dish) },
                        onDelete = { onDeleteDish(dish.id) }
                    )
                }

                AddDishButton(
                    onClick = {
                        onShowDishDialog(null)
                        onClearFieldError(MealEditorField.DISH_LIST)
                    },
                    enabled = !uiState.isLoading
                )
            }
        }

        if (uiState.showDishDialog) {
            DishEditorDialog(
                state = uiState.dishEditorState,
                isEditMode = uiState.dishEditorState.dishId != 0,
                onDismiss = onDismissDishDialog,
                onNameChange = onDishNameChange,
                onDescriptionChange = onDishDescriptionChange,
                onPriceChange = onDishPriceChange,
                onRatingChange = onDishRatingChange,
                onDishTypeChange = onDishTypeChange,
                onExpandedChange = onDishTypeExpandedChange,
                onSave = onSaveDish
            )
        }

        // Photos section
        Column(modifier = modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.feature_meal_photos_label),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                if (uiState.showAddButtonPhoto) {
                    AddPhotoButton(
                        onClick = onShowPhotoSelectionBottomSheet
                    )
                } else {
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
                dishList = listOf(
                    Dish(
                        id = 1,
                        name = "Steak",
                        rating = 5f,
                        description = "Grilled steak",
                        price = 18.50,
                        dishType = com.jeromedusanter.restorik.core.model.DishType.MAIN_COURSE
                    ),
                    Dish(
                        id = 2,
                        name = "Fries",
                        rating = 4.5f,
                        description = "Crispy fries",
                        price = 6.00,
                        dishType = com.jeromedusanter.restorik.core.model.DishType.SIDE_DISH
                    )
                ),
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
            onSelectRestaurantSuggestion = {},
            onClearFieldError = {},
            onDeletePhoto = {},
            onShowPhotoSelectionBottomSheet = {},
            onSelectPhotoForView = {},
            onClearSelectedPhoto = {},
            onSaveMeal = {},
            onMoveFocusDown = {},
            onDeleteDish = {},
            onShowDishDialog = {},
            onDismissDishDialog = {},
            onDishNameChange = {},
            onDishDescriptionChange = {},
            onDishPriceChange = {},
            onDishRatingChange = {},
            onDishTypeChange = {},
            onDishTypeExpandedChange = {},
            onSaveDish = {}
        )
    }
}