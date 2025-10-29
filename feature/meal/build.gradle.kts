plugins {
    alias(libs.plugins.restorik.android.feature)
    alias(libs.plugins.restorik.android.library.compose)
}

android {
    namespace = "com.jeromedusanter.restorik.feature.meal"
}

dependencies {
    implementation(libs.androidx.compose.material.iconsExtended)
}