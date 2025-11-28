package com.jeromedusanter.restorik.feature.meal.detail

import com.jeromedusanter.restorik.feature.meal.editor.MealEditorUiState

data class MealDetailUiState(
    val title: String,
) {
    companion object {
        val EMPTY = MealDetailUiState(
            title = ""
        )
    }
}