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
    moduleType: org.hnau.commons.plugins.project.utils.ModuleType,
) {
    val config = SharedConfig
        .extractFromRootProject(project)
        .toProjectConfig(project)

    val projectType: ProjectType = when (moduleType) {
        ModuleType.JVM -> configureJvm(
            config = config,
            addAndroid = false,
        )

        ModuleType.KMP -> configureKmp(
            config = config,
            addCompose = false,
        )

        ModuleType.UI -> configureKmp(
            config = config,
            addCompose = true,
        )

        ModuleType.ANDROID_APP -> configureJvm(
            config = config,
            addAndroid = true,
        )

        ModuleType.PLUGINS -> configurePlugins()
    }

    configureCommon(
        config = config,
        projectType = projectType,
    )
}