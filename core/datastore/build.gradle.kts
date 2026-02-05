plugins {
    alias(libs.plugins.restorik.android.library)
    alias(libs.plugins.restorik.hilt)
}

android {
    namespace = "com.jeromedusanter.restorik.core.datastore"
}

dependencies {
    api(libs.androidx.dataStore)
}