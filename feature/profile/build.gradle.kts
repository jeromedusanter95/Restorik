plugins {
    alias(libs.plugins.restorik.android.feature)
    alias(libs.plugins.restorik.android.library.compose)
}

android {
    namespace = "com.jeromedusanter.restorik.feature.profile"
}

dependencies {
    implementation(projects.core.domain)
    implementation(libs.androidx.compose.material.iconsExtended)

    // Testing
    testImplementation(projects.core.testing)
}
