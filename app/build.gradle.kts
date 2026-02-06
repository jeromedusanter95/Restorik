plugins {
    alias(libs.plugins.restorik.android.application)
    alias(libs.plugins.restorik.android.application.compose)
    alias(libs.plugins.restorik.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.jeromedusanter.restorik"

    defaultConfig {
        applicationId = "com.jeromedusanter.restorik"
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.designSystem)
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.feature.meal)
    implementation(projects.feature.search)
    implementation(projects.feature.profile)
    implementation(projects.core.camera)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.window.core)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.coil.kt)
    implementation(libs.kotlinx.serialization.json)
    ksp(libs.hilt.compiler)
    androidTestImplementation(libs.androidx.compose.ui.test)
    implementation(libs.androidx.compose.material.iconsExtended)
}