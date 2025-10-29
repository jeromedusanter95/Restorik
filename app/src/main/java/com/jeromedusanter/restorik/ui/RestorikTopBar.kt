package com.jeromedusanter.restorik.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestorikTopBar(
    modifier: Modifier = Modifier,
    title: String,
    shouldShowBackButton: Boolean = false,
    onBackButtonClick: () -> Unit = {},
    onSearchButtonClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        modifier = modifier,
        navigationIcon = {
            if (shouldShowBackButton) {
                IconButton(onClick = onBackButtonClick) {
                    Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Back")
                }
            } else {
                IconButton(onClick = onSearchButtonClick) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
            }
        },
    )
}