pluginManagement {
    includeBuild("build-logic") // Before configuring my app, compile the stuff in build-logic/ and make any plugins it exposes available by ID
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Restorik"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS") // Permit to use projects.core.X in build.gradle.kts
include(":app")
include(":core:model")
include(":core:ui")
include(":core:design-system")
include(":feature:meal")
include(":core:data")
include(":core:database")
include(":core:camera")
include(":feature:restaurant")
