package org.hnau.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class ComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configureHnau(HnauProjectType.COMPOSE)
    }
}
