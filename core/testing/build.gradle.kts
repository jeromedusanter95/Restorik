plugins {
    alias(libs.plugins.restorik.android.library)
    alias(libs.plugins.restorik.hilt)
}

android {
    namespace = "com.jeromedusanter.restorik.core.testing"
}

dependencies {
    api(project(":core:data"))
    api(project(":core:model"))

    // Coroutines Test
    api(libs.kotlinx.coroutines.test)

    // JUnit
    api(libs.junit)

    // Kotlin Test
    api(libs.kotlin.test)
}
