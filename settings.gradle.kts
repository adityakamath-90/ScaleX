pluginManagement {
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
//    pluginManagement {
//        includeBuild("build-logic")
//    }

}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Reddit"
include(":app")
include(":home")
include(":core:designsystem")
include(":features")
include(":features:feed")
include(":core:analytics")
include(":core:auth:auth-impl")
include(":core:test")
include(":core:network:network-impl")
include(":core:network:network-api")
include(":core:auth:auth-api")
include(":sdks:networkSdk")
