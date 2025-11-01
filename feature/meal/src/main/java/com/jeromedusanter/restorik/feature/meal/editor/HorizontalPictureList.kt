package com.jeromedusanter.restorik.feature.meal.editor

import android.net.Uri
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HorizontalPictureList(
    modifier: Modifier = Modifier,
    pictureList: List<Uri>,
    onClickDelete: (Uri) -> Unit
) {
    LazyRow {
        items(pictureList) { it ->
            PictureItem(
                modifier = modifier,
                uri = it,
                onClickDelete = onClickDelete
            )
        }
    }
}


