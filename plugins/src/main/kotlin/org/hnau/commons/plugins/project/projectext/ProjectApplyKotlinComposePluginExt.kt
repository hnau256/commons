package org.hnau.commons.plugins.project.projectext

import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import org.hnau.commons.plugins.Versions
import org.hnau.commons.plugins.project.utils.Constants
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.applyKotlinComposePlugin() {
    applyPlugin(Versions.Plugins.kotlinCompose.withoutAlias.withoutVersion)

    val stabilityConfigPath = project
        .layout
        .buildDirectory
        .file(Constants.composeStabilityConfigBuildFileName)

    project
        .extensions
        .getByType<ComposeCompilerGradlePluginExtension>()
        .apply { stabilityConfigurationFiles.add(stabilityConfigPath) }

    project
        .tasks
        .register(
            Constants.copyHnauComposeStabilityConfigTaskName,
            CopyComposeStabilityConfigTask::class.java,
        ) { task ->
            task.outputFile.set(stabilityConfigPath)
            task.inputResourcePath.set(Constants.composeStabilityConfigResourcesFileName)
        }

    project
        .tasks
        .withType(KotlinCompile::class.java)
        .configureEach { task ->
            task.dependsOn(Constants.copyHnauComposeStabilityConfigTaskName)
        }
}

internal abstract class CopyComposeStabilityConfigTask : org.gradle.api.DefaultTask() {
    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:org.gradle.api.tasks.Internal
    abstract val inputResourcePath: org.gradle.api.provider.Property<String>

    @TaskAction
    fun execute() {
        val resourcePath = inputResourcePath.get()
        val classLoader = this.javaClass.classLoader

        val configContent = classLoader
            .getResourceAsStream(resourcePath)
            ?.use { it.readBytes() }
            ?: throw IllegalStateException("$resourcePath not found in plugin resources")

        outputFile
            .get()
            .asFile
            .apply { parentFile.mkdirs() }
            .writeBytes(configContent)
    }
}
