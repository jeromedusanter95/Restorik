plugins {
    alias(libs.plugins.restorik.android.library.compose)
}

android {
    namespace = "com.jeromedusanter.restorik.core.camera"
}

dependencies {
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)
    implementation(libs.coil.kt.compose)
}