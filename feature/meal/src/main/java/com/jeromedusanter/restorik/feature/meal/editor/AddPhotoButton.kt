package com.jeromedusanter.restorik.feature.meal.editor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme
import com.jeromedusanter.restorik.core.ui.RestorikDashedButton
import com.jeromedusanter.restorik.feature.meal.R

@Composable
fun AddPhotoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RestorikDashedButton(
        text = stringResource(R.string.feature_meal_add_photo_button),
        imageVector = Icons.Default.AddAPhoto,
        onClick = onClick,
        modifier = modifier
    )
}

@Preview
@Composable
private fun AddPhotoButtonPreview() {
    RestorikTheme {
        AddPhotoButton(onClick = {})
    }
}
