package com.jeromedusanter.restorik.core.camera

sealed class CameraRoutes(val route: String) {
    data object Capture : CameraRoutes("camera/capture")
    data object Preview : CameraRoutes("camera/preview?uri={uri}")

}