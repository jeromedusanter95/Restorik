package com.jeromedusanter.restorik.core.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class CameraActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Full screen
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(
            WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars()
        )

        setContent {
            val activity = LocalContext.current as Activity

            var hasCameraPermission by remember {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(
                        activity, Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                )
            }

            val requestPermission = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->
                hasCameraPermission = granted
                if (!granted) activity.finish()
            }

            LaunchedEffect(Unit) {
                if (!hasCameraPermission) {
                    requestPermission.launch(Manifest.permission.CAMERA)
                }
            }

            if (!hasCameraPermission) return@setContent

            val navController = rememberNavController()

            RestorikTheme {
                NavHost(
                    navController = navController,
                    startDestination = CameraRoutes.Capture.route
                ) {
                    composable(CameraRoutes.Capture.route) {
                        CameraScreen(
                            onClose = { finish() },
                            onPhotoTaken = { uri ->
                                navController.navigate("camera/preview?uri=${Uri.encode(uri.toString())}")
                            }
                        )
                    }
                    composable(
                        route = CameraRoutes.Preview.route,
                        arguments = listOf(navArgument("uri") { type = NavType.StringType })
                    ) { entry ->
                        val uri = entry.arguments?.getString("uri")?.toUri()
                        CameraPreviewScreen(
                            photoUri = uri,
                            onConfirm = {
                                setResult(RESULT_OK, Intent().setData(uri))
                                finish()
                            },
                            onGoBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
