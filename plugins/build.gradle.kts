plugins {
    id(hnau.plugins.hnau.plugins.get().pluginId)
}

gradlePlugin {
    plugins {
        val prefix = "org.hnau.plugin"

        create("HnauSettings") {
            id = "$prefix.settings"
            implementationClass = "org.hnau.commons.plugins.settings.HnauSettingsPlugin"
            displayName = "Hnau Settings Plugin"
            description =
                "Centralized settings: version catalog, pluginManagement, auto-include modules, allModules defaults"
        }
        create("HnauJvm") {
            id = "$prefix.jvm"
            implementationClass = "org.hnau.commons.plugins.project.entrypoints.HnauJvmPlugin"
            displayName = "Hnau JVM Plugin"
            description = "Kotlin JVM module configuration with auto-detection"
        }
        create("HnauKmp") {
            id = "$prefix.kmp"
            implementationClass = "org.hnau.commons.plugins.project.entrypoints.HnauKmpPlugin"
            displayName = "Hnau KMP Plugin"
            description = "Kotlin Multiplatform module configuration with auto-detection"
        }
        create("HnauUi") {
            id = "$prefix.ui"
            implementationClass = "org.hnau.commons.plugins.project.entrypoints.HnauUiPlugin"
            displayName = "Hnau UI Plugin"
            description = "Compose Multiplatform module configuration with auto-detection"
        }
        create("HnauAndroidApp") {
            id = "$prefix.androidapp"
            implementationClass = "org.hnau.commons.plugins.project.entrypoints.HnauAndroidAppPlugin"
            displayName = "Hnau Android App Plugin"
            description = "Android Application module configuration with auto-detection"
        }
        create("HnauPlugins") {
            id = "$prefix.plugins"
            implementationClass = "org.hnau.commons.plugins.project.entrypoints.HnauPluginsPlugin"
            displayName = "Hnau Gradle plugins Plugin"
            description = "Gradle plugins module configuration with auto-detection"
        }
    }
}