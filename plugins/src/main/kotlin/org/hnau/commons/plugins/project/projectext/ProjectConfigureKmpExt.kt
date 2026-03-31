package org.hnau.commons.plugins.project.projectext

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.hnau.commons.plugins.Versions
import org.hnau.commons.plugins.project.utils.Constants
import org.hnau.commons.plugins.project.utils.ProjectConfig
import org.hnau.commons.plugins.project.utils.ProjectType
import org.hnau.commons.plugins.project.utils.androidNamespace
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureKmp(
    config: ProjectConfig,
    addCompose: Boolean,
): ProjectType.Kmp {
    applyPlugin(Versions.Plugins.kotlinMultiplatform.withoutAlias.withoutVersion)

    project
        .extensions
        .getByType(KotlinMultiplatformExtension::class.java)
        .jvmToolchain(Versions.jvmTargetInt)

    val projectType = ProjectType.Kmp(
        kmpExtension = extensions.getByType(KotlinMultiplatformExtension::class.java)
    )

    projectType.kmpExtension.compilerOptions {
        freeCompilerArgs.addAll(Constants.kotlinFreeCompilerArgs)
    }

    when (addCompose) {
        true -> {
            applyPlugin(Versions.Plugins.androidMultiplatformLibrary.withoutAlias.withoutVersion)
            applyPlugin(Versions.Plugins.composeMultiplatform.withoutAlias.withoutVersion)
            applyKotlinComposePlugin()

            addAndroidDependencies(
                projectType = projectType,
                addCompose = true,
            )

            (projectType.kmpExtension as ExtensionAware)
                .extensions
                .getByType(KotlinMultiplatformAndroidLibraryExtension::class.java)
                .apply {
                    namespace = config.androidNamespace
                    compileSdk = Versions.compileSdk
                    minSdk = Versions.minSdk
                }

            projectType
                .kmpExtension
                .jvm(Constants.desktopTargetName) {
                    withSourcesJar()
                }

            dependencies.add(
                "${Constants.desktopTargetName}MainImplementation",
                ComposePlugin.Dependencies(project).desktop.currentOs,
            )

            addComposeDependencies(
                dependencies = Versions.composeMultiplatform,
                projectType = projectType,
            )
        }

        false -> {

            projectType
                .kmpExtension
                .jvm {
                    withSourcesJar()
                }

            projectType
                .kmpExtension
                .linuxX64()
        }
    }

    return projectType
}