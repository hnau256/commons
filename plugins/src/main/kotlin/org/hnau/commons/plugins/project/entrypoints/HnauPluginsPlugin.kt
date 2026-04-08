package org.hnau.commons.plugins.project.entrypoints

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.hnau.commons.plugins.project.configureForHnau
import org.hnau.commons.plugins.project.utils.ModuleType

class HnauPluginsPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        project.configureForHnau(
            moduleType = ModuleType.Plugins,
        )
    }
}
