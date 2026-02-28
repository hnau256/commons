package org.hnau.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.configureHnau(HnauProjectType.JVM)
}
