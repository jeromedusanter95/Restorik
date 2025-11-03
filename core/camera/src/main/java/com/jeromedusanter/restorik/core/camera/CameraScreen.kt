package com.jeromedusanter.restorik.core.camera

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.concurrent.Executors

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {},
    onPhotoTaken: (android.net.Uri) -> Unit = {},
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Camera controller + executor
    val controller = rememberCameraController(context)
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // Flash state mirrors controller state
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_OFF) }
    LaunchedEffect(flashMode) { controller.imageCaptureFlashMode = flashMode }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        CameraPreviewView(
            controller = controller,
            bindLifecycle = { controller.bindToLifecycle(lifecycleOwner) }
        )
        CameraTopBar(
            flashMode = flashMode,
            onClose = onClose,
            onToggleFlash = {
                flashMode = when (flashMode) {
                    ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
                    ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
                    else -> ImageCapture.FLASH_MODE_OFF
                }
            },
            onSwitchCamera = {
                controller.cameraSelector =
                    if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    else
                        CameraSelector.DEFAULT_BACK_CAMERA
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .align(Alignment.TopCenter)
        )
        CaptureButton(
            modifier = Modifier
                .size(84.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp),
            onCapture = {
                captureToMediaStore(
                    context = context,
                    controller = controller,
                    executor = cameraExecutor,
                    onSaved = { uri -> onPhotoTaken(uri) },
                    onError = { e -> Log.e("Camera", "Photo capture failed", e) }
                )
            }
        )
    }
}


@Composable
private fun CameraPreviewView(
    controller: LifecycleCameraController,
    bindLifecycle: () -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
                bindLifecycle()
                this.controller = controller
            }
        }
    )
}

@Composable
private fun CameraTopBar(
    flashMode: Int,
    onClose: () -> Unit,
    onToggleFlash: () -> Unit,
    onSwitchCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onClose,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Black.copy(alpha = 0.35f)
            )
        ) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            val flashIcon = when (flashMode) {
                ImageCapture.FLASH_MODE_OFF -> Icons.Filled.FlashOff
                ImageCapture.FLASH_MODE_ON -> Icons.Filled.FlashOn
                else -> Icons.Filled.Bolt // AUTO indicator
            }
            IconButton(
                onClick = onToggleFlash,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.35f)
                )
            ) { Icon(flashIcon, contentDescription = "Flash", tint = Color.White) }

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = onSwitchCamera,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.35f)
                )
            ) {
                Icon(
                    Icons.Filled.Cameraswitch,
                    contentDescription = "Switch camera",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun CaptureButton(
    modifier: Modifier = Modifier,
    onCapture: () -> Unit
) {
    FilledIconButton(
        onClick = onCapture,
        modifier = modifier,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        )
    ) {
        Icon(
            Icons.Filled.CameraAlt,
            contentDescription = "Take picture",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun rememberCameraController(context: Context): LifecycleCameraController =
    remember(context) {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(LifecycleCameraController.IMAGE_CAPTURE)
            imageCaptureMode = ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
        }
    }

/**
 * Captures an image to MediaStore and invokes callbacks on the main thread.
 */
private fun captureToMediaStore(
    context: Context,
    controller: LifecycleCameraController,
    executor: java.util.concurrent.Executor,
    onSaved: (android.net.Uri) -> Unit,
    onError: (Throwable) -> Unit
) {
    val name = "IMG_${System.currentTimeMillis()}"
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, name)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Restorik")
        }
    }
    val outputOptions = ImageCapture.OutputFileOptions
        .Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        .build()

    controller.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                output.savedUri?.let { uri ->
                    Handler(Looper.getMainLooper()).post { onSaved(uri) }
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Handler(Looper.getMainLooper()).post { onError(exception) }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun CameraScreenPreview() {
    RestorikTheme { CameraScreen() }
}
