plugins {
    id(
        hnau.plugins.hnau.plugins
            .get()
            .pluginId,
    )
}

// Task to generate version constant from settings
val generateVersionConstant by tasks.registering {
    val version = rootProject.extra["hnauCommonsVersion"] as String
    val outputDir = layout.buildDirectory.dir("generated-src/kotlin/main")
    val outputFile = outputDir.map { it.file("org/hnau/commons/plugins/internal/HnauCommonsVersion.kt") }

    inputs.property("version", version)
    outputs.file(outputFile)

    doLast {
        outputFile.get().asFile.apply {
            parentFile.mkdirs()
            writeText(
                """
                package org.hnau.commons.plugins.internal
                
                const val hnauCommonsVersion: String = "$version"
                """.trimIndent(),
            )
        }
    }
}

// Add generated source directory to Kotlin source sets
kotlin {
    sourceSets.main {
        kotlin.srcDir(layout.buildDirectory.dir("generated-src/kotlin/main"))
    }
}

// Ensure compileKotlin depends on version generation
tasks.compileKotlin {
    dependsOn(generateVersionConstant)
}

// Configure task dependencies after evaluation
afterEvaluate {
    tasks.findByName("sourcesJar")?.dependsOn(generateVersionConstant)
    tasks.findByName("dokkaGeneratePublicationHtml")?.dependsOn(generateVersionConstant)
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

        create("HnauJvmAndroidApp") {
            id = "$prefix.jvmAndroidApp"
            implementationClass = "org.hnau.commons.plugins.project.entrypoints.HnauJvmAndroidAppPlugin"
            displayName = "Hnau Jvm Android App Plugin"
            description = "JVM Android Application module configuration with auto-detection"
        }

        create("HnauKmp") {
            id = "$prefix.kmp"
            implementationClass = "org.hnau.commons.plugins.project.entrypoints.HnauKmpPlugin"
            displayName = "Hnau KMP Plugin"
            description = "Kotlin Multiplatform module configuration with auto-detection"
        }

        create("HnauKmpAndroid") {
            id = "$prefix.kmpAndroid"
            implementationClass = "org.hnau.commons.plugins.project.entrypoints.HnauKmpAndroidPlugin"
            displayName = ""
            description = ""
        }

        create("HnauKmpAndroidWithCompose") {
            id = "$prefix.kmpAndroidWithCompose"
            implementationClass = "org.hnau.commons.plugins.project.entrypoints.HnauKmpAndroidWithComposePlugin"
            displayName = ""
            description = ""
        }

        create("HnauPlugins") {
            id = "$prefix.plugins"
            implementationClass = "org.hnau.commons.plugins.project.entrypoints.HnauPluginsPlugin"
            displayName = "Hnau Gradle plugins Plugin"
            description = "Gradle plugins module configuration with auto-detection"
        }
    }
}
