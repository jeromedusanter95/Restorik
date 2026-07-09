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
        versionCode = 3
        versionName = "1.0.2"
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

    flavorDimensions += "environment"
    productFlavors {
        create("prod") {
            dimension = "environment"
            // Ships as the base applicationId "com.jeromedusanter.restorik".
        }
        create("dev") {
            dimension = "environment"
            // Installs alongside prod with its own id (".dev") and the
            // "Restorik Dev" launcher name (see src/dev/res).
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
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
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.kotlinx.serialization.json)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.compose.ui.test)
    implementation(libs.androidx.compose.material.iconsExtended)
}