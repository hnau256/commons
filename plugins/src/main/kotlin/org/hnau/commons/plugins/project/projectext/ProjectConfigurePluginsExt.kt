package org.hnau.commons.plugins.project.projectext

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.gradleKotlinDsl
import org.hnau.commons.plugins.Versions
import org.hnau.commons.plugins.project.utils.ProjectType

internal fun Project.configurePlugins(): ProjectType {

    val projectType = ProjectType.Jvm(
        isPlugins = true,
    )

    applyPlugin(Versions.Plugins.javaGradlePlugin)
    applyPlugin(Versions.Plugins.kotlinJvm.withoutAlias.withoutVersion)

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