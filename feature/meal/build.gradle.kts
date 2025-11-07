plugins {
    alias(libs.plugins.restorik.android.feature)
    alias(libs.plugins.restorik.android.library.compose)
}

android {
    namespace = "com.jeromedusanter.restorik.feature.meal"
}

dependencies {
    implementation(projects.core.camera)
    implementation(projects.core.domain)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.coil.kt.compose)
    implementation(libs.zoomable.image.coil)
}