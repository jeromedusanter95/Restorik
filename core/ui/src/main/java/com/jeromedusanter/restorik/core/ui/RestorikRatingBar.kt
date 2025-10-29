package com.jeromedusanter.restorik.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme

@Composable
fun RestorikRatingBar(
    value: Int,
    onValueChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxRating: Int = 5
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxRating) {
            val isSelected = i <= value
            IconButton(onClick = { onValueChanged(i) }) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "$i stars",
                    tint = if (isSelected)
                        Color(0xFFFFC107)
                    else
                        Color.LightGray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RestorikRatingBarPreview() {
    RestorikTheme {
        var rating by remember { mutableIntStateOf(3) }

        RestorikRatingBar(
            value = rating,
            onValueChanged = { rating = it }
        )
    }
}
