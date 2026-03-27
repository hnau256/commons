rootProject.name = "commons"

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    val versionFile = file("version.properties")
    if (!versionFile.exists()) {
        error("version.properties not found at ${versionFile.absolutePath}")
    }

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

    val projectVersion = props["version"]
        ?: error("'version' property not found in version.properties")


    settings.extra["parsedProjectVersion"] = projectVersion

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.hnau.plugin.settings") {
                val pluginVersion = props["pluginVersion"] ?: projectVersion
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
