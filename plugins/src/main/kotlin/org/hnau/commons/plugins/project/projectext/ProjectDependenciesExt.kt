package org.hnau.commons.plugins.project.projectext

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.hnau.commons.plugins.Versions
import org.hnau.commons.plugins.project.utils.ProjectType
import org.hnau.commons.plugins.utils.versions.ComposeDependencyType
import org.hnau.commons.plugins.utils.versions.ComposeDependencyTypeValues
import org.hnau.commons.plugins.utils.versions.LibraryId
import org.hnau.commons.plugins.utils.versions.PluginId
import org.hnau.commons.plugins.utils.versions.Versioned

internal fun Project.applyPlugin(
    plugin: PluginId,
) {
    plugins.apply(
        plugin.id,
    )
}

internal fun Project.addDependency(
    type: ProjectType,
    dependency: Versioned<LibraryId>,
) {
    when (type) {
        is ProjectType.Jvm -> addDependency("implementation", dependency)
        is ProjectType.Kmp -> type
            .commonMainSourceSet
            .configure { commonMainSourceSet ->
                commonMainSourceSet.dependencies {
                    implementation(dependency.asDependency)
                }
            }
    }
}

internal fun Project.addTestDependency(
    type: ProjectType,
    dependency: Versioned<LibraryId>,
) {
    when (type) {
        is ProjectType.Jvm -> addDependency("testImplementation", dependency)
        is ProjectType.Kmp -> type
            .commonTestSourceSet
            .configure { commonTestSourceSet ->
                commonTestSourceSet.dependencies {
                    implementation(dependency.asDependency)
                }
            }
    }
}

internal fun Project.addDependency(
    configurationName: String,
    dependency: Versioned<LibraryId>,
) {
    dependencies {
        add(
            configurationName,
            dependency.asDependency,
        )
    }
}

internal fun Project.addComposeDependencies(
    dependencies: ComposeDependencyTypeValues<Versioned<LibraryId>>,
    projectType: ProjectType,
) {
    ComposeDependencyType
        .entries
        .map(dependencies::get)
        .forEach { jetpackComposeDependency ->
            addDependency(
                type = projectType,
                dependency = jetpackComposeDependency,
            )
        }
}

internal fun Project.addAndroidDependencies(
    projectType: ProjectType,
    addCompose: Boolean,
) {
    buildList {
        addAll(Versions.Android.unconditioned)
        if (addCompose) {
            addAll(Versions.Android.unconditionedCompose)
        }
    }.forEach { dependency ->
        addDependency(
            configurationName = when (projectType) {
                is ProjectType.Jvm -> "implementation"
                is ProjectType.Kmp -> "androidMainImplementation"
            },
            dependency = dependency,
        )
    }
}

internal val Versioned<LibraryId>.asDependency: String
    get() = listOf(
        withoutVersion.groupId.groupId,
        withoutVersion.artifactId.artifactId,
        version.version
    ).joinToString(
        separator = ":",
    )

internal fun Project.hasPlugin(
    plugin: PluginId,
): Boolean = plugins.hasPlugin(
    plugin.id,
)