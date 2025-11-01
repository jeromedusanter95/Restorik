package com.jeromedusanter.restorik.feature.meal.editor

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HorizontalPhotoList(
    modifier: Modifier = Modifier,
    photoUriList: List<Uri>,
    showAddPhotoItem: Boolean,
    onClickDelete: (Uri) -> Unit = {},
    onClickAdd: () -> Unit = {},
    onClickItem: (Uri) -> Unit = {}
) {
    LazyRow {
        items(photoUriList) { uri ->
            PhotoItem(
                modifier = modifier.clickable(onClick = { onClickItem(uri) }),
                uri = uri,
                onClickDelete = onClickDelete
            )
        }
        if (showAddPhotoItem) {
            item { AddPhotoItem(onClickAdd = onClickAdd) }
        }
    }
}


