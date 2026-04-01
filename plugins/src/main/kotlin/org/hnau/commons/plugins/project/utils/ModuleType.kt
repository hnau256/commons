package org.hnau.commons.plugins.project.utils

sealed interface ModuleType {

    data class Jvm(
        val isAndroidApp: Boolean,
    ) : ModuleType

    data class Kmp(
        val level: Level,
    ) : ModuleType {

        sealed interface Level {
            data object Pure : Level

            data class Android(
                val withCompose: Boolean,
            ): Level
        }
    }

    data object Plugins : ModuleType
}
