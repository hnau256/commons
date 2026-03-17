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
    id("org.hnau.plugin.settings") version "1.2.11"
}

hnau {
    groupId = "org.hnau.commons"
    publish {
        version = "1.4.2"
        gitUrl = "https://github.com/hnau256/commons"
    }
}
