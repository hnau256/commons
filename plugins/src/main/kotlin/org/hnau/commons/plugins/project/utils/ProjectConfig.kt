package org.hnau.commons.plugins.project.utils

import org.gradle.api.Project
import org.hnau.commons.plugins.utils.SharedConfig
import org.hnau.commons.plugins.utils.versions.ArtifactId
import org.hnau.commons.plugins.utils.versions.GroupId

data class ProjectConfig(
    val groupId: GroupId,
    val artifactId: ArtifactId,
    val publish: Publish?,
) {

    data class Publish(
        val gitUrl: String,
        val version: String,
        val description: String,
        val developerName: String,
        val developerEmail: String,
        val licenseName: String,
        val licenseUrl: String,
    )
}

internal fun SharedConfig.toProjectConfig(
    project: Project,
): ProjectConfig {

    val artifactId: ArtifactId = listOf(
        projectId
            .projectId
            .let { projectId -> listOf(projectId) },
        project
            .path
            .split(':')
            .filter(String::isNotEmpty)
    )
        .flatten()
        .joinToString(
            separator = "-",
        )
        .let(::ArtifactId)

    return ProjectConfig(
        groupId = GroupId("org.hnau.$projectId"),
        artifactId = artifactId,
        publish = publish?.run {
            ProjectConfig.Publish(
                gitUrl = gitUrl,
                version = version,
                description = description ?: artifactId.artifactId,
                developerName = developerName,
                developerEmail = developerEmail,
                licenseName = licenseName,
                licenseUrl = licenseUrl,
            )
        }
    )
}

internal val ProjectConfig.androidNamespace: String
    get() = listOf(
        groupId.groupId,
        artifactId.artifactId.replace('-', '.')
    ).joinToString(
        separator = ".",
    )