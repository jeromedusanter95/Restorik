package com.jeromedusanter.restorik.feature.meal.editor

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme

@Composable
fun PhotoItem(
    modifier: Modifier = Modifier,
    uri: Uri,
    onClickDelete: (Uri) -> Unit = {},
) {
    val imageSize = 64.dp
    val iconSize = 24.dp
    Box(modifier = modifier.size(imageSize + iconSize)) {
        AsyncImage(
            model = uri,
            contentDescription = null,
            modifier = Modifier
                .size(imageSize)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(size = 12.dp)),
            contentScale = ContentScale.Crop,
        )
        Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .size(iconSize)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.primary)
                .clickable(onClick = { onClickDelete(uri) }),
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun PictureItemPreview() {
    RestorikTheme {
        PhotoItem(uri = Uri.EMPTY)
    }
}