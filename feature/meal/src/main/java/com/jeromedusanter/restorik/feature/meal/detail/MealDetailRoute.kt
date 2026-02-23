package com.jeromedusanter.restorik.feature.meal.detail

import android.net.Uri
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.jeromedusanter.restorik.feature.meal.R
import com.jeromedusanter.restorik.feature.meal.navigation.MEAL_EDITED_RESULT_KEY
import kotlinx.coroutines.launch

@Composable
fun MealDetailRoute(
    modifier: Modifier = Modifier,
    viewModel: MealDetailViewModel = hiltViewModel(),
    onMealDeleted: () -> Unit = {},
    navController: NavHostController,
    snackbarHostState: SnackbarHostState
) {
    val uiState = viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    val mealEditedSuccessMessage = stringResource(R.string.feature_meal_meal_edited_successfully)
    val photoDownloadedSuccessMessage = stringResource(R.string.feature_meal_photo_downloaded_successfully)
    val photoDownloadFailedMessage = stringResource(R.string.feature_meal_photo_download_failed)

    LaunchedEffect(Unit) {
        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getStateFlow(MEAL_EDITED_RESULT_KEY, false)?.collect { mealEdited ->
            if (mealEdited) {
                snackbarHostState.showSnackbar(
                    message = mealEditedSuccessMessage,
                    duration = SnackbarDuration.Short
                )
                savedStateHandle[MEAL_EDITED_RESULT_KEY] = false
            }
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