plugins {
    alias(libs.plugins.restorik.android.library)
    alias(libs.plugins.restorik.android.library.compose)
}

android {
    namespace = "com.jeromedusanter.restorik.core.designsystem"
}

dependencies {
    implementation(libs.androidx.compose.material3)
}