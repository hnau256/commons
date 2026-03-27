package org.hnau.commons.plugins.project.projectext

import org.gradle.api.Project
import org.hnau.commons.plugins.Versions
import org.hnau.commons.plugins.project.utils.ProjectType

internal fun Project.configureSerializationIfNeed(
    projectType: ProjectType,
) {
    if (!hasPlugin(Versions.Plugins.kotlinSerialization.withoutAlias.withoutVersion)) {
        return
    }

    Versions
        .Kotlinx
        .serialization
        .forEach { dependency ->
            addDependency(
                type = projectType,
                dependency = dependency,
            )
        }
}