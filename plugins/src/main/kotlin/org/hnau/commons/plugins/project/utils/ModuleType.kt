package org.hnau.commons.plugins.project.utils

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.desktop.DesktopExtension

sealed interface ModuleType {

    fun disablePublicationAfterEvaluate(
        project: Project,
    ): Boolean = false

    data class Jvm(
        val isAndroidApp: Boolean,
    ) : ModuleType {

        override fun disablePublicationAfterEvaluate(
            project: Project,
        ): Boolean = isAndroidApp
    }

    data class Kmp(
        val level: Level,
    ) : ModuleType {

        sealed interface Level {
            data object Pure : Level

            data class Android(
                val withCompose: Boolean,
            ) : Level
        }

        override fun disablePublicationAfterEvaluate(
            project: Project,
        ): Boolean = when (level) {
            Level.Pure -> false
            is Level.Android -> when (level.withCompose) {
                false -> false
                true -> {
                    project
                        .extensions
                        .findByType<ComposeExtension>()
                        ?.let { it as? ExtensionAware }
                        ?.extensions
                        ?.findByType<DesktopExtension>()
                        ?.application
                        ?.mainClass
                        .orEmpty()
                        .trim()
                        .isNotEmpty()
                }
            }
        }
    }

    data object Plugins : ModuleType
}
