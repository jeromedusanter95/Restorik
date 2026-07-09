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
import com.jeromedusanter.restorik.core.ui.navigation.Navigator
import com.jeromedusanter.restorik.core.ui.navigation.ResultEffect
import com.jeromedusanter.restorik.core.ui.navigation.ResultEventBus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealEditorRoute(
    mealId: Int,
    modifier: Modifier = Modifier,
    viewModel: MealEditorViewModel = hiltViewModel<MealEditorViewModel, MealEditorViewModel.Factory>(
        creationCallback = { factory -> factory.create(mealId = mealId) }
    ),
    onMealSaved: () -> Unit = {},
    snackbarHostState: SnackbarHostState,
    resultBus: ResultEventBus,
    navigator: Navigator
) {

    val uiState = viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    val captureLauncher = rememberLauncherForActivityResult(contract = CapturePhotoContract()) { uri ->
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
    LaunchedEffect(key1 = uiState.value.errorMessage) {
        uiState.value.errorMessage?.let { errorMessage ->
            snackbarHostState.showSnackbar(message = errorMessage)
            viewModel.clearError()
        }
    }

    // Observe save trigger from TopAppBar via ResultEventBus
    ResultEffect<Boolean>(resultEventBus = resultBus, resultKey = "trigger_save_meal") { shouldSave ->
        if (shouldSave) {
            viewModel.saveMeal(onSaveMealSuccess = onMealSaved)
        }
    }

    MealEditorScreen(
        uiState = uiState.value,
        onNameChanged = viewModel::updateMealName,
        onDateTimeChanged = viewModel::updateMealDateTime,
        onRestaurantNameChanged = viewModel::updateRestaurantName,
        onSelectRestaurantSuggestion = viewModel::selectRestaurantSuggestion,
        onCityNameChanged = viewModel::updateCityName,
        onSelectCitySuggestion = viewModel::selectCitySuggestion,
        onClearFieldError = viewModel::clearFieldError,
        onDeletePhoto = viewModel::deletePhoto,
        onShowPhotoSelectionBottomSheet = viewModel::showPhotoSelectionBottomSheet,
        onSelectPhotoForView = viewModel::selectPhotoForView,
        onClearSelectedPhoto = viewModel::clearSelectedPhoto,
        onDownloadPhoto = viewModel::downloadPhoto,
        onMoveFocusDown = { focusManager.moveFocus(focusDirection = FocusDirection.Down) },
        onDeleteDish = viewModel::deleteDish,
        onShowDishDialog = viewModel::showDishDialog,
        onDismissDishDialog = viewModel::dismissDishDialog,
        onDishNameChanged = viewModel::updateDishName,
        onDishDescriptionChanged = viewModel::updateDishDescription,
        onDishPriceChanged = viewModel::updateDishPrice,
        onDishRatingChanged = viewModel::updateDishRating,
        onDishTypeChanged = viewModel::updateDishType,
        onDishTypeExpandedChanged = viewModel::setDishTypeExpanded,
        onSaveDish = viewModel::saveDishFromEditor,
        onIsSomeoneElsePayingChanged = viewModel::setIsSomeoneElsePaying,
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