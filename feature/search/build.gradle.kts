plugins {
    alias(libs.plugins.restorik.android.feature)
    alias(libs.plugins.restorik.android.library.compose)
}

android {
    namespace = "com.jeromedusanter.restorik.feature.search"
}

dependencies {
    implementation(projects.core.domain)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.coil.kt.compose)
}
