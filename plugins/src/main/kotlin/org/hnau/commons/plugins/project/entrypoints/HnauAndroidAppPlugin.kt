package org.hnau.commons.plugins.project.entrypoints

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.hnau.commons.plugins.project.utils.ModuleType
import org.hnau.commons.plugins.project.configureForHnau

class HnauAndroidAppPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.configureForHnau(ModuleType.ANDROID_APP)
    }
}
