rootProject.name = "commons"

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

plugins {
    id("org.hnau.plugin.settings") version "1.4.3"
}

val versionFile = file("version.properties")
require(versionFile.exists()) { "version.properties not found at ${versionFile.absolutePath}" }
val projectVersion = versionFile.readText().trim()

hnau {
    groupId = "org.hnau.commons"
    publish {
        version = projectVersion
        gitUrl = "https://github.com/hnau256/commons"
    }
}
