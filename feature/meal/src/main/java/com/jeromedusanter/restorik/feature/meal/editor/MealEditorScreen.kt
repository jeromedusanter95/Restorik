package com.jeromedusanter.restorik.feature.meal.editor

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jeromedusanter.restorik.feature.meal.R
import com.jeromedusanter.restorik.core.camera.CapturePhotoContract
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme
import com.jeromedusanter.restorik.core.ui.AddPhotoButton
import com.jeromedusanter.restorik.core.ui.RestorikOutlineTextField
import com.jeromedusanter.restorik.core.ui.RestorikRatingBar

@Composable
fun MealEditorScreen(
    modifier: Modifier = Modifier,
    viewModel: MealEditorViewModel = hiltViewModel(),
    onMealSaved: () -> Unit = {},
    snackbarHostState: SnackbarHostState
) {

    val uiState = viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val captureLauncher = rememberLauncherForActivityResult(CapturePhotoContract()) { uri ->
        if (uri != null) {
            viewModel.addPhoto(uri)
        }
    }

    // Show error snackbar when error message is present
    LaunchedEffect(uiState.value.errorMessage) {
        uiState.value.errorMessage?.let { errorMessage ->
            snackbarHostState.showSnackbar(errorMessage)
            viewModel.clearError()
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
                onValueChange = { newValue ->
                    viewModel.updateMealName(newValue)
                    viewModel.clearFieldError(MealEditorField.MEAL_NAME)
                },
                label = stringResource(R.string.feature_meal_meal_name_label),
                enabled = !uiState.value.isLoading,
                isRequired = true,
                isError = uiState.value.fieldErrors.mealNameError != null,
                supportingText = uiState.value.fieldErrors.mealNameError,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            RestorikOutlineTextField(
                modifier = modifier.fillMaxWidth(),
                value = uiState.value.restaurantName,
                onValueChange = { newValue ->
                    viewModel.updateRestaurantName(newValue)
                    viewModel.clearFieldError(MealEditorField.RESTAURANT_NAME)
                },
                label = stringResource(R.string.feature_meal_restaurant_name_label),
                enabled = !uiState.value.isLoading,
                isRequired = true,
                isError = uiState.value.fieldErrors.restaurantNameError != null,
                supportingText = uiState.value.fieldErrors.restaurantNameError,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            RestorikOutlineTextField(
                modifier = modifier.fillMaxWidth(),
                value = uiState.value.comment,
                onValueChange = viewModel::updateMealComment,
                singleLine = false,
                label = stringResource(R.string.feature_meal_comment_label),
                enabled = !uiState.value.isLoading,
            )

            RestorikOutlineTextField(
                modifier = modifier.fillMaxWidth(),
                value = uiState.value.priceAsString,
                onValueChange = { newValue ->
                    viewModel.updatePrice(newValue)
                    viewModel.clearFieldError(MealEditorField.PRICE)
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
                    onDone = { focusManager.clearFocus() }
                ),
                label = stringResource(R.string.feature_meal_price_label),
                enabled = !uiState.value.isLoading,
                isRequired = true,
                isError = uiState.value.fieldErrors.priceError != null,
                supportingText = uiState.value.fieldErrors.priceError
            )

            Text(stringResource(R.string.feature_meal_rating_label))
            RestorikRatingBar(
                modifier = modifier.align(alignment = Alignment.CenterHorizontally),
                value = uiState.value.ratingOnFive,
                onValueChanged = viewModel::updateRating
            )


            if (uiState.value.showAddButtonPhoto) {
                AddPhotoButton(
                    onClick = { captureLauncher.launch(Unit) }
                )
            } else {
                Text("${stringResource(R.string.feature_meal_photos_label)} ${uiState.value.photoTitleSuffix}")
                HorizontalPhotoList(
                    photoUriList = uiState.value.photoUriList,
                    showAddPhotoItem = uiState.value.showAddButtonPhotoItem,
                    onClickDelete = viewModel::deletePhoto,
                    onClickAdd = { captureLauncher.launch(Unit) },
                    onClickItem = { uri -> selectedPhotoUri = uri }
                )
                selectedPhotoUri?.let { photoUri ->
                    PhotoViewDialog(
                        photoUri = photoUri,
                        onDismiss = { selectedPhotoUri = null }
                    )
                }
            }

            Button(
                onClick = { viewModel.saveMeal { onMealSaved() } },
                modifier = modifier.align(alignment = Alignment.CenterHorizontally),
                enabled = !uiState.value.isLoading
            ) {
                if (uiState.value.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(stringResource(R.string.feature_meal_save_button))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MealEditorScreenPreview() {
    RestorikTheme {
        MealEditorScreen(snackbarHostState = remember { SnackbarHostState() })
    }
}