package org.hnau.commons.plugins.project.entrypoints

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.hnau.commons.plugins.project.configureForHnau
import org.hnau.commons.plugins.project.utils.ModuleType

class HnauKmpPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.configureForHnau(ModuleType.KMP)
    }
}
