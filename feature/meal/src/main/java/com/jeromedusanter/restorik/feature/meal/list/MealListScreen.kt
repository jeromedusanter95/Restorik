package com.jeromedusanter.restorik.feature.meal.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme
import com.jeromedusanter.restorik.feature.meal.R
import com.jeromedusanter.restorik.feature.meal.navigation.MEAL_SAVED_RESULT_KEY
import com.jeromedusanter.restorik.feature.meal.navigation.navigateToMealDetail

@Composable
fun MealListScreen(
    modifier: Modifier = Modifier,
    viewModel: MealListViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    navController: NavHostController
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val mealAddedSuccessMessage = stringResource(R.string.feature_meal_meal_added_successfully)

    LaunchedEffect(lifecycleOwner) {
        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getStateFlow(MEAL_SAVED_RESULT_KEY, false)?.collect { mealSaved ->
            if (mealSaved) {
                snackbarHostState.showSnackbar(
                    message = mealAddedSuccessMessage,
                    duration = SnackbarDuration.Short
                )
                savedStateHandle[MEAL_SAVED_RESULT_KEY] = false
            }
        }
    }

    val uiState = viewModel.uiState.collectAsState()

    when {
        uiState.value.isLoading -> {
            MealListLoadingState()
        }

        uiState.value.groupedMealList.isEmpty() -> {
            MealListEmptyState()
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(all = 16.dp),
                verticalArrangement = Arrangement.spacedBy(space = 8.dp)
            ) {
                uiState.value.groupedMealList.forEach { group ->
                    item(key = "header_${group.title}") {
                        Text(
                            text = group.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 4.dp)
                        )
                    }

                    itemsIndexed(
                        items = group.mealList,
                        key = { _, meal -> meal.id }
                    ) { index, meal ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically(
                                initialOffsetY = { it / 2 }
                            )
                        ) {
                            MealListItem(
                                mealUiModel = meal,
                                onClickItem = { navController.navigateToMealDetail(mealId = meal.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MealListScreenPreview() {
    RestorikTheme {
        MealListScreen(
            snackbarHostState = remember { SnackbarHostState() },
            navController = rememberNavController()
        )
    }
}