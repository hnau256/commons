package org.hnau.commons.plugins.project.projectext

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.hnau.commons.plugins.Versions
import org.hnau.commons.plugins.project.utils.Constants
import org.hnau.commons.plugins.project.utils.ModuleType
import org.hnau.commons.plugins.project.utils.ProjectConfig
import org.hnau.commons.plugins.project.utils.ProjectType
import org.hnau.commons.plugins.project.utils.androidNamespace
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

internal fun Project.configureKmp(
    config: ProjectConfig,
    level: ModuleType.Kmp.Level,
): ProjectType.Kmp {
    applyPlugin(Versions.Plugins.kotlinMultiplatform.withoutAlias.withoutVersion)

    val projectType = ProjectType.Kmp(
        kmpExtension = extensions.getByType(KotlinMultiplatformExtension::class.java),
    )

    tasks.withType(KotlinJvmCompile::class.java).configureEach { task ->
        task.compilerOptions {
            jvmTarget.set(Versions.jvmTarget)
        }
    }

    projectType.kmpExtension.compilerOptions {
        freeCompilerArgs.addAll(Constants.kotlinFreeCompilerArgs)
    }

    @OptIn(ExperimentalWasmDsl::class)
    fun configSourceSets(
        withCompose: Boolean,
    ) {
        with(
            receiver = projectType.kmpExtension,
        ) {

            when (withCompose) {
                true -> jvm(Constants.desktopTargetName) { withSourcesJar() }
                false -> {
                    jvm { withSourcesJar() }
                    linuxX64()
                }
            }

            wasmJs {
                nodejs()
            }
        }
    }

    when (level) {
        ModuleType.Kmp.Level.Pure -> {
            configSourceSets(
                withCompose = false,
            )
        }

        is ModuleType.Kmp.Level.Android -> {

            configSourceSets(
                withCompose = level.withCompose,
            )

            applyPlugin(Versions.Plugins.androidMultiplatformLibrary.withoutAlias.withoutVersion)

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

            if (level.withCompose) {
                applyPlugin(Versions.Plugins.composeMultiplatform.withoutAlias.withoutVersion)
                applyKotlinComposePlugin()

                dependencies.add(
                    Constants.desktopMainImplementation,
                    ComposePlugin.Dependencies(project).desktop.currentOs,
                )

                dependencies.add(
                    "androidRuntimeClasspath",
                    Versions.ComposeMultiplatform.uiToolingPreview.asDependency,
                )

                dependencies.add(
                    "androidMainImplementation",
                    Versions.ComposeMultiplatform.uiTooling.asDependency,
                )

                dependencies.add(
                    Constants.desktopMainImplementation,
                    Versions.ComposeMultiplatform.uiTooling.asDependency,
                )

                addComposeDependencies(
                    dependencies = Versions.ComposeMultiplatform.dependencies,
                    projectType = projectType,
                )

            }
        }
    }

    return projectType
}
