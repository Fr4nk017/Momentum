pluginManagement {
    repositories { gradlePluginPortal(); google(); mavenCentral() }
}

plugins {
    // Permite a Gradle resolver/descargar JDKs automáticamente (Foojay)
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral() }
}
rootProject.name = "Momentum"
include(":app")
