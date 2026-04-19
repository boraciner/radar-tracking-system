rootProject.name = "radar-tracking-system"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        mavenCentral()
    }
}

include(
    "naming-service",
    "radar-service",
    "plot-listener-service",
    "tracker-service",
    "map-viewer-service"
)
