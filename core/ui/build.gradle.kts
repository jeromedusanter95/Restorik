plugins {
    alias(libs.plugins.restorik.android.library)
    alias(libs.plugins.restorik.android.library.compose)
}

android {
    namespace = "com.jeromedusanter.restorik.core.ui"
}

dependencies {
    api(projects.core.designSystem)
}