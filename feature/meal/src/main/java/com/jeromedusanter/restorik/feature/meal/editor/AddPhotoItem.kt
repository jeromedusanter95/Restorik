package com.jeromedusanter.restorik.feature.meal.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun AddPhotoItem(
    modifier: Modifier = Modifier,
    onClickAdd: () -> Unit = {}
) {
    val imageSize = 64.dp
    val iconSize = 24.dp
    val cornerRadius = 12.dp

    Column {
        Spacer(Modifier.height(iconSize / 2))
        Row {
            Spacer(Modifier.width(iconSize / 2))
            val accentColor = MaterialTheme.colorScheme.primary
            Box(
                modifier = modifier
                    .size(imageSize)
                    .clip(RoundedCornerShape(size = cornerRadius))
                    .clickable(onClick = onClickAdd)
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val stroke = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                    )
                    drawRoundRect(
                        color = accentColor,
                        style = stroke,
                        cornerRadius = CornerRadius(x = cornerRadius.toPx())
                    )
                }
                Row(modifier = Modifier.align(Alignment.Center)) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = null,
                        tint = accentColor
                    )
                }
            }
        }
    }
}