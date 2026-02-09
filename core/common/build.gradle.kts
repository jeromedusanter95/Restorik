plugins {
    alias(libs.plugins.restorik.android.library)
    alias(libs.plugins.restorik.hilt)
}

android {
    namespace = "com.jeromedusanter.restorik.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
