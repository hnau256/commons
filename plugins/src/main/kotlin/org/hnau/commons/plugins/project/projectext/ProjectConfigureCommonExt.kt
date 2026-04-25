package org.hnau.commons.plugins.project.projectext

import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.kotlin.dsl.getByType
import org.hnau.commons.plugins.Versions
import org.hnau.commons.plugins.project.utils.ProjectConfig
import org.hnau.commons.plugins.project.utils.ProjectType
import org.hnau.commons.plugins.project.utils.isCommons

internal fun Project.configureCommon(
    config: ProjectConfig,
    projectType: ProjectType,
    disablePublicationAfterEvaluate: () -> Boolean,
) {

    if (!config.isCommons) {
        addDependency(
            type = projectType,
            dependency = Versions.HnauCommons.kotlin,
        )
    }

    project
        .extensions
        .getByType<BasePluginExtension>()
        .apply { archivesName.set(config.artifactId.artifactId) }

    buildList {
        addAll(Versions.Arrow.unconditioned)
        addAll(Versions.Kotlinx.unconditioned)
        addAll(Versions.Standalone.unconditioned)
    }.forEach { arrowDependency ->
        addDependency(
            type = projectType,
            dependency = arrowDependency,
        )
    }

    addTestDependency(
        type = projectType,
        dependency = Versions.kotlinTest,
    )

    configureSerializationIfNeed(
        projectType = projectType,
    )

    val hasKsp = hasPlugin(Versions.Plugins.ksp.withoutAlias.withoutVersion)
    if (hasKsp) {
        configureKsp(
            projectType = projectType,
            config = config,
        )
    }

    config.publish?.let { publish ->
        configurePublishing(
            publish = publish,
            projectConfig = config,
            projectType = projectType,
            hasKsp = hasKsp,
            disableAfterEvaluate = disablePublicationAfterEvaluate,
        )
    }
}
