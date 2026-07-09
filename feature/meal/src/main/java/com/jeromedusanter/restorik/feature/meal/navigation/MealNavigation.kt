package com.jeromedusanter.restorik.feature.meal.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.jeromedusanter.restorik.core.ui.navigation.Navigator
import com.jeromedusanter.restorik.core.ui.navigation.ResultEventBus
import com.jeromedusanter.restorik.feature.meal.detail.MealDetailRoute
import com.jeromedusanter.restorik.feature.meal.editor.MealEditorRoute
import com.jeromedusanter.restorik.feature.meal.list.MealListRoute

fun EntryProviderScope<NavKey>.mealSection(
    navigator: Navigator,
    resultBus: ResultEventBus,
    snackbarHostState: SnackbarHostState
) {
    entry<MealList> {
        MealListRoute(
            snackbarHostState = snackbarHostState,
            navigator = navigator,
            resultBus = resultBus
        )
    }

    entry<MealDetail> { key ->
        MealDetailRoute(
            mealId = key.mealId,
            onMealDeleted = {
                resultBus.sendResult(resultKey = "meal_deleted_result", result = true)
                navigator.goBack()
            },
            navigator = navigator,
            snackbarHostState = snackbarHostState
        )
    }

    entry<MealEditor> { key ->
        val isEditMode = key.mealId != -1

        MealEditorRoute(
            mealId = key.mealId,
            onMealSaved = {
                resultBus.sendResult(
                    resultKey = if (isEditMode) "meal_edited_result" else "meal_saved_result",
                    result = true
                )
                navigator.goBack()
            },
            snackbarHostState = snackbarHostState,
            resultBus = resultBus,
            navigator = navigator
        )
    }
}
