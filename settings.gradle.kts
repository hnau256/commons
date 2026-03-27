rootProject.name = "commons"

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.hnau.plugin.settings") {
                // Read version.properties
                val versionFile = file("version.properties")
                if (!versionFile.exists()) {
                    error("version.properties not found at ${versionFile.absolutePath}")
                }

                // Parse properties manually
                val lines = versionFile.readLines()
                val props = mutableMapOf<String, String>()
                for (line in lines) {
                    val trimmed = line.trim()
                    if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
                        val parts = trimmed.split("=", limit = 2)
                        if (parts.size == 2) {
                            props[parts[0].trim()] = parts[1].trim()
                        }
                    }
                }

                val projectVersion =
                    props["version"]
                        ?: error("'version' property not found in version.properties")

                // pluginVersion is optional - falls back to projectVersion if not specified
                val pluginVersion = props["pluginVersion"] ?: projectVersion

                useVersion(pluginVersion)
            }
        }
    }
}

plugins {
    id("org.hnau.plugin.settings")
}

// Read version for hnau extension
val versionFile = file("version.properties")
if (!versionFile.exists()) {
    error("version.properties not found at ${versionFile.absolutePath}")
}

// Parse properties
val lines = versionFile.readLines()
val props = mutableMapOf<String, String>()
for (line in lines) {
    val trimmed = line.trim()
    if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
        val parts = trimmed.split("=", limit = 2)
        if (parts.size == 2) {
            props[parts[0].trim()] = parts[1].trim()
        }
    }
}

val projectVersion =
    props["version"]
        ?: error("'version' property not found in version.properties")

hnau {
    groupId = "org.hnau.commons"
    publish {
        version = projectVersion
        gitUrl = "https://github.com/hnau256/commons"
    }
}
