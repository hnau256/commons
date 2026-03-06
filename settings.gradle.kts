pluginManagement {
    includeBuild("../plugins")
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("org.hnau.settings") version "0.1.0"
}

rootProject.name = "commons"

hnauSettings {
    allModules {
        group = "org.hnau.commons"
        version = "1.2.3"
        includeHnauCommons = false
        gitUrl = "https://github.com/hnau256/commons"
    }
}
