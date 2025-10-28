plugins {
    alias(libs.plugins.restorik.android.library)
    alias(libs.plugins.restorik.hilt)
}

android {
    namespace = "com.jeromedusanter.restorik.core.data"
}

dependencies {
    implementation(projects.core.model)
}