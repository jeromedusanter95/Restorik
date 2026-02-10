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
    // All common dependencies are now in AndroidFeatureConventionPlugin
}
