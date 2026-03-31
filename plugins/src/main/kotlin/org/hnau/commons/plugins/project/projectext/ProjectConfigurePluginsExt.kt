package org.hnau.commons.plugins.project.projectext

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.gradleKotlinDsl
import org.hnau.commons.plugins.Versions
import org.hnau.commons.plugins.project.utils.ProjectConfig
import org.hnau.commons.plugins.project.utils.ProjectType

internal fun Project.configurePlugins(
    config: ProjectConfig,
): ProjectType {

    configureJvm(
        config = config,
        addAndroid = false,
    )

    val projectType = ProjectType.Jvm(
        isPlugins = true,
    )

    applyPlugin(Versions.Plugins.javaGradlePlugin)

    dependencies {
        add("compileOnly", gradleApi())
        add("compileOnly", gradleKotlinDsl())
    }

    Versions.pluginsAsLibraries.forEach { pluginAsLibrary ->
        addDependency(
            type = projectType,
            dependency = pluginAsLibrary,
        )
    }

    return projectType
}