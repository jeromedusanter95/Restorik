package com.jeromedusanter.restorik.feature.meal.editor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme
import com.jeromedusanter.restorik.core.ui.RestorikDashedButton
import com.jeromedusanter.restorik.feature.meal.R

@Composable
fun AddDishButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    RestorikDashedButton(
        text = stringResource(id = R.string.feature_meal_add_dish_button),
        imageVector = Icons.Default.Add,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    )
}

@Preview
@Composable
private fun AddDishButtonPreview() {
    RestorikTheme {
        AddDishButton(onClick = {})
    }
}
