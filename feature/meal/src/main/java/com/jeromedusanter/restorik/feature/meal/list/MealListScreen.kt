package com.jeromedusanter.restorik.feature.meal.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme
import com.jeromedusanter.restorik.feature.meal.navigation.MEAL_SAVED_RESULT_KEY

@Composable
fun MealListScreen(
    modifier: Modifier = Modifier,
    viewModel: MealListViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    navController: NavHostController
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getStateFlow(MEAL_SAVED_RESULT_KEY, false)?.collect { mealSaved ->
            if (mealSaved) {
                snackbarHostState.showSnackbar(
                    message = "Meal added successfully",
                    duration = SnackbarDuration.Short
                )
                savedStateHandle[MEAL_SAVED_RESULT_KEY] = false
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Meal List Screen !")
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