plugins {
    alias(libs.plugins.restorik.android.library)
}

android {
    namespace = "com.jeromedusanter.restorik.core.domain"
}

dependencies {
    api(projects.core.data)
    api(projects.core.model)
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)
}