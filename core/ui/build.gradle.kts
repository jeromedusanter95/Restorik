plugins {
    alias(libs.plugins.restorik.android.library)
    alias(libs.plugins.restorik.android.library.compose)
}

android {
    namespace = "com.jeromedusanter.restorik.core.ui"
}

dependencies {
    implementation(projects.core.designSystem)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.zoomable.image.coil)
}