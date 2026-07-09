package com.jeromedusanter.restorik.feature.meal.detail

import android.net.Uri
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.jeromedusanter.restorik.core.ui.navigation.Navigator
import com.jeromedusanter.restorik.core.ui.navigation.ResultEffect
import com.jeromedusanter.restorik.feature.meal.R
import kotlinx.coroutines.launch

@Composable
fun MealDetailRoute(
    mealId: Int,
    modifier: Modifier = Modifier,
    viewModel: MealDetailViewModel = hiltViewModel<MealDetailViewModel, MealDetailViewModel.Factory>(
        creationCallback = { factory -> factory.create(mealId = mealId) }
    ),
    onMealDeleted: () -> Unit = {},
    navigator: Navigator,
    snackbarHostState: SnackbarHostState
) {
    val uiState = viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    val mealEditedSuccessMessage = stringResource(id = R.string.feature_meal_meal_edited_successfully)
    val photoDownloadedSuccessMessage = stringResource(id = R.string.feature_meal_photo_downloaded_successfully)
    val photoDownloadFailedMessage = stringResource(id = R.string.feature_meal_photo_download_failed)

    // Handle meal edited result
    ResultEffect<Boolean>(resultKey = "meal_edited_result") { mealEdited ->
        if (mealEdited) {
            snackbarHostState.showSnackbar(
                message = mealEditedSuccessMessage,
                duration = SnackbarDuration.Short
            )
        }
    }

    MealDetailScreen(
        modifier = modifier,
        uiState = uiState.value,
        onDeleteClick = {
            viewModel.deleteMeal(onSuccess = onMealDeleted)
        },
        onDownloadPhoto = { uri: Uri ->
            scope.launch {
                val success = viewModel.downloadPhoto(uri = uri)
                snackbarHostState.showSnackbar(
                    message = if (success) photoDownloadedSuccessMessage else photoDownloadFailedMessage,
                    duration = SnackbarDuration.Short
                )
            }
        }
    )
}