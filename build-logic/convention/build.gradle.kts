import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.jeromedusanter.restorik.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradleApiPlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplicationCompose") {
            id = libs.plugins.restorik.android.application.compose.get().pluginId
            implementationClass = "com.jeromedusanter.convention.AndroidApplicationComposeConventionPlugin"
        }
        register("androidApplication") {
            id = libs.plugins.restorik.android.application.asProvider().get().pluginId
            implementationClass = "com.jeromedusanter.convention.AndroidApplicationConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = libs.plugins.restorik.android.library.compose.get().pluginId
            implementationClass = "com.jeromedusanter.convention.AndroidLibraryComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.restorik.android.library.asProvider().get().pluginId
            implementationClass = "com.jeromedusanter.convention.AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = libs.plugins.restorik.android.feature.get().pluginId
            implementationClass = "com.jeromedusanter.convention.AndroidFeatureConventionPlugin"
        }
        register("hilt") {
            id = libs.plugins.restorik.hilt.get().pluginId
            implementationClass = "com.jeromedusanter.convention.HiltConventionPlugin"
        }
        register("androidRoom") {
            id = libs.plugins.restorik.android.room.get().pluginId
            implementationClass = "com.jeromedusanter.convention.AndroidRoomConventionPlugin"
        }
        register("jvmLibrary") {
            id = libs.plugins.restorik.jvm.library.get().pluginId
            implementationClass = "com.jeromedusanter.convention.JvmLibraryConventionPlugin"
        }
    }
}