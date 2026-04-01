package org.hnau.commons.plugins.project.entrypoints

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.hnau.commons.plugins.project.utils.ModuleType
import org.hnau.commons.plugins.project.configureForHnau

class HnauJvmPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        project.configureForHnau(
            moduleType = ModuleType.Jvm(
                isAndroidApp = false,
            )
        )
    }
}
