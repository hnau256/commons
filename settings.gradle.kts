pluginManagement {
    includeBuild("build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "commons"

fun findAndIncludeModules(
    dir: File,
    pathPrefix: String = "",
) {
    dir
        .listFiles { file ->
            file.isDirectory &&
                !file.name.startsWith(".") &&
                file.name != "build" &&
                file.name != "gradle" &&
                file.name != "build-logic"
        }?.forEach { file ->
            val currentPath =
                listOfNotNull(pathPrefix.takeIf(String::isNotEmpty), file.name)
                    .joinToString(separator = ":")
            when (File(file, "build.gradle.kts").exists()) {
                true -> include(":$currentPath")
                false -> findAndIncludeModules(file, currentPath)
            }
        }
}

findAndIncludeModules(rootDir)
