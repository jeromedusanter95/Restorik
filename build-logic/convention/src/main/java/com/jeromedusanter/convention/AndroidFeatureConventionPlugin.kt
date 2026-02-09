package com.jeromedusanter.convention

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "restorik.android.library")
            apply(plugin = "restorik.hilt")
            apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

            extensions.configure<LibraryExtension> {
                testOptions.animationsDisabled = true
            }

            dependencies {
                "implementation"(project(":core:common"))
                "implementation"(project(":core:ui"))
                "implementation"(project(":core:design-system"))
                "implementation"(project(":core:model"))
                "implementation"(project(":core:data"))

                "implementation"(libs.findLibrary("androidx.hilt.navigation.compose").get())
                "implementation"(libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
                "implementation"(libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
                "implementation"(libs.findLibrary("androidx.navigation.compose").get())
                "implementation"(libs.findLibrary("androidx.tracing.ktx").get())
                "implementation"(libs.findLibrary("kotlinx.serialization.json").get())

                // Unit Tests (JVM - src/test/)
                "testImplementation"(libs.findLibrary("junit").get())
                "testImplementation"(libs.findLibrary("kotlin.test").get())
                "testImplementation"(libs.findLibrary("kotlinx.coroutines.test").get())
                "testImplementation"(libs.findLibrary("truth").get())
                "testImplementation"(libs.findLibrary("turbine").get())
                "testImplementation"(libs.findLibrary("mockk.android").get())
                "testImplementation"(libs.findLibrary("androidx.navigation.testing").get())

                // Android Instrumented Tests (device/emulator - src/androidTest/)
                "androidTestImplementation"(libs.findLibrary("junit").get())
                "androidTestImplementation"(libs.findLibrary("androidx.test.core").get())
                "androidTestImplementation"(libs.findLibrary("androidx.test.ext").get())
                "androidTestImplementation"(libs.findLibrary("androidx.test.rules").get())
                "androidTestImplementation"(libs.findLibrary("androidx.test.runner").get())
                "androidTestImplementation"(libs.findBundle("androidx.compose.ui.test").get())
                "androidTestImplementation"(libs.findLibrary("androidx.lifecycle.runtimeTesting").get())
                "androidTestImplementation"(libs.findLibrary("kotlinx.coroutines.test").get())
                "androidTestImplementation"(libs.findLibrary("truth").get())
                "androidTestImplementation"(libs.findLibrary("hilt.android.testing").get())
                "androidTestImplementation"(libs.findLibrary("mockk.android").get())
                "androidTestImplementation"(libs.findLibrary("mockk.agent").get())
            }
        }
    }
}
