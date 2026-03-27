rootProject.name = "commons"

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    data class Properties(
        val version: String,
        val pluginVersion: String?,
    )

    val propertiesFile = file("version.properties").let { file ->
        file
            .takeIf(File::exists)
            ?: error("version.properties not found at ${file.absolutePath}")
    }

    val properties = propertiesFile
        .useLines { lines ->
            lines
                .map(String::trim)
                .filter(String::isNotEmpty)
                .filterNot { it.startsWith('#') }
                .associate { line ->
                    line
                        .split("=", limit = 2)
                        .let { parts ->
                            parts
                                .takeIf { it.size == 2 }
                                ?: error("Incorrect format of properties line: '$line'")
                        }
                        .map(String::trim)
                        .let { (key, value) -> key to value }
                }
        }
        .let { propertiesMap ->
            Properties(
                version = propertiesMap["version"]
                    ?: error("Unable found 'version' property in file ${propertiesFile.absolutePath}"),
                pluginVersion = propertiesMap["pluginVersion"]
            )
        }

    val projectVersion = properties.version

    settings.extra["parsedProjectVersion"] = projectVersion

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.hnau.plugin.settings") {
                val pluginVersion = properties.pluginVersion ?: projectVersion
                useVersion(pluginVersion)
            }
        }
    }
}

plugins {
    id("org.hnau.plugin.settings")
}

hnau {
    groupId = "org.hnau.commons"
    publish {
        version = settings.extra["parsedProjectVersion"] as String
        gitUrl = "https://github.com/hnau256/commons"
    }
}

gradle.projectsLoaded {
    rootProject.extra["hnauCommonsVersion"] = settings.extra["parsedProjectVersion"] as String
}
