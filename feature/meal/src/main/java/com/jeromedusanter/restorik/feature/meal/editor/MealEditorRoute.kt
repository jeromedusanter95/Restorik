package com.jeromedusanter.restorik.feature.meal.editor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.jeromedusanter.restorik.core.camera.CapturePhotoContract

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealEditorRoute(
    modifier: Modifier = Modifier,
    viewModel: MealEditorViewModel = hiltViewModel(),
    onMealSaved: () -> Unit = {},
    snackbarHostState: SnackbarHostState
) {

    val uiState = viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    val captureLauncher = rememberLauncherForActivityResult(CapturePhotoContract()) { uri ->
        if (uri != null) {
            viewModel.addPhoto(uri = uri)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.addPhoto(uri = uri)
        }
    }

    val bottomSheetState = rememberModalBottomSheetState()

    // Show error snackbar when error message is present
    LaunchedEffect(uiState.value.errorMessage) {
        uiState.value.errorMessage?.let { errorMessage ->
            snackbarHostState.showSnackbar(errorMessage)
            viewModel.clearError()
        }
    }

    MealEditorScreen(
        uiState = uiState.value,
        onNameChange = viewModel::updateMealName,
        onRestaurantNameChange = viewModel::updateRestaurantName,
        onSelectRestaurantSuggestion = viewModel::selectRestaurantSuggestion,
        onClearFieldError = viewModel::clearFieldError,
        onDeletePhoto = viewModel::deletePhoto,
        onShowPhotoSelectionBottomSheet = viewModel::showPhotoSelectionBottomSheet,
        onSelectPhotoForView = viewModel::selectPhotoForView,
        onClearSelectedPhoto = viewModel::clearSelectedPhoto,
        onSaveMeal = { viewModel.saveMeal(onSaveMealSuccess = onMealSaved) },
        onMoveFocusDown = { focusManager.moveFocus(focusDirection = FocusDirection.Down) },
        onDeleteDish = viewModel::deleteDish,
        onShowDishDialog = viewModel::showDishDialog,
        onDismissDishDialog = viewModel::dismissDishDialog,
        onDishNameChange = viewModel::updateDishName,
        onDishDescriptionChange = viewModel::updateDishDescription,
        onDishPriceChange = viewModel::updateDishPrice,
        onDishRatingChange = viewModel::updateDishRating,
        onDishTypeChange = viewModel::updateDishType,
        onDishTypeExpandedChange = viewModel::setDishTypeExpanded,
        onSaveDish = viewModel::saveDishFromEditor,
        modifier = modifier
    )

    if (uiState.value.showPhotoSelectionBottomSheet) {
        PhotoSelectionBottomSheet(
            onDismiss = viewModel::hidePhotoSelectionBottomSheet,
            onTakePhoto = { captureLauncher.launch(Unit) },
            onChooseFromGallery = {
                galleryLauncher.launch(
                    PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            sheetState = bottomSheetState
        )
    }
}