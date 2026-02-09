plugins {
    alias(libs.plugins.restorik.android.feature)
    alias(libs.plugins.restorik.android.library.compose)
}

android {
    namespace = "com.jeromedusanter.restorik.feature.profile"

    packaging {
        resources {
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {
    implementation(projects.core.domain)
    implementation(libs.androidx.compose.material.iconsExtended)

    // Testing
    testImplementation(projects.core.testing)
    androidTestImplementation(projects.core.testing)
    androidTestImplementation(libs.androidx.compose.ui.test)

    // Force Espresso 3.7.0 to override transitive dependency from Compose UI test
    androidTestImplementation(libs.androidx.test.espresso.core)
}
