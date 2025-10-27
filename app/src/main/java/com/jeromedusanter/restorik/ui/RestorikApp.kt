package com.jeromedusanter.restorik.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.jeromedusanter.restorik.navigation.RestorikNavHost

@Composable
fun RestorikApp(modifier: Modifier = Modifier, ) {

    val navController = rememberNavController()

    Scaffold(
        modifier = modifier,
        topBar = {},
        bottomBar = {},
        snackbarHost = {},
        floatingActionButton = {},
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        RestorikNavHost(
            modifier = modifier.padding(innerPadding),
            navController = navController
        )
    }
}