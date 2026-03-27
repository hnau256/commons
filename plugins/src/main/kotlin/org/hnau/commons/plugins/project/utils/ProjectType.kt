package org.hnau.commons.plugins.project.utils

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

sealed interface ProjectType {

    data class Jvm(
        val isPlugins: Boolean,
    ): ProjectType

    data class Kmp(
        val kmpExtension: KotlinMultiplatformExtension,
    ): ProjectType
}