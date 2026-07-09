package com.jeromedusanter.convention

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "org.jetbrains.kotlin.android")
            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 36
                testOptions.animationsDisabled = true
            }

            dependencies {
                "implementation"(libs.findLibrary("androidx.navigation3.runtime").get())
                "implementation"(libs.findLibrary("androidx.navigation3.ui").get())
            }
        }
    }
}
