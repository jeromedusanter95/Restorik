package com.jeromedusanter.restorik.core.camera

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme

@Composable
fun CameraPreviewScreen(
    modifier: Modifier = Modifier,
    photoUri: Uri? = null,
    onConfirm: () -> Unit = {},
    onGoBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            IconButton(
                onClick = onGoBack,
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(0.35f))
            ) {
                Icon(
                    Icons.Filled.ArrowBackIosNew,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }

            Text(
                text = "Prévisualisation",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center),
                fontWeight = FontWeight.Bold
            )
        }

        Image(
            modifier = modifier.weight(1.0f),
            painter = rememberAsyncImagePainter(photoUri),
            contentDescription = "Preview",
            contentScale = ContentScale.Fit
        )

        Button(onClick = onConfirm, modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Filled.Check, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Confirmer")
        }
    }
}

@Preview
@Composable
private fun CameraPreviewScreenPreview() {
    RestorikTheme {
        CameraPreviewScreen()
    }
}
