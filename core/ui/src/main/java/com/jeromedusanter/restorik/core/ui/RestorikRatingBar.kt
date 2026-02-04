package com.jeromedusanter.restorik.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme
import com.jeromedusanter.restorik.core.designsystem.theme.gold
import kotlin.math.roundToInt

@Composable
fun RestorikRatingBar(
    value: Float,
    onValueChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    maxRating: Int = 5,
    enabled: Boolean = true,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp)
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxRating) {
            val fillPercentage = when {
                value >= i -> 1f
                value > i - 1 -> value - (i - 1)
                else -> 0f
            }

            if (enabled) {
                IconButton(
                    onClick = { onValueChanged(i.toFloat()) }
                ) {
                    PartialStar(fillPercentage = fillPercentage)
                }
            } else {
                PartialStar(fillPercentage = fillPercentage)
            }
        }
    }
}

@Composable
private fun PartialStar(fillPercentage: Float) {
    Box {
        Icon(
            imageVector = Icons.Outlined.StarOutline,
            contentDescription = null,
            tint = Color.LightGray
        )
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = gold,
            modifier = Modifier
                .clipToBounds()
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints = constraints)
                    val width = (placeable.width * fillPercentage).roundToInt()
                    layout(width = width, height = placeable.height) {
                        placeable.place(x = 0, y = 0)
                    }
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RestorikRatingBarPreview() {
    RestorikTheme {
        var rating by remember { mutableFloatStateOf(3.5f) }

        RestorikRatingBar(
            value = rating,
            onValueChanged = { rating = it }
        )
    }
}
