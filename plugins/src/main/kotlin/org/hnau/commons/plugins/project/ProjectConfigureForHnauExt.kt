package org.hnau.commons.plugins.project

import org.gradle.api.Project
import org.hnau.commons.plugins.project.projectext.configureCommon
import org.hnau.commons.plugins.project.projectext.configureJvm
import org.hnau.commons.plugins.project.projectext.configureKmp
import org.hnau.commons.plugins.project.projectext.configurePlugins
import org.hnau.commons.plugins.project.utils.ModuleType
import org.hnau.commons.plugins.project.utils.ProjectType
import org.hnau.commons.plugins.project.utils.toProjectConfig
import org.hnau.commons.plugins.utils.SharedConfig

internal fun Project.configureForHnau(
    moduleType: ModuleType,
) {

    val config = SharedConfig
        .extractFromRootProject(project)
        .toProjectConfig(project)

    val projectType: ProjectType = when (moduleType) {

        is ModuleType.Jvm -> configureJvm(
            config = config,
            isAndroidApp = moduleType.isAndroidApp,
        )

        is ModuleType.Kmp -> configureKmp(
            config = config,
            level = moduleType.level,
        )

        ModuleType.Plugins -> configurePlugins(
            config = config,
        )
    }

    configureCommon(
        config = config,
        projectType = projectType,
    )
}