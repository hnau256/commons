package org.hnau.commons.app.model.app.utils

import arrow.core.getOrElse
import arrow.core.toOption
import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.app.model.app.AppContext
import org.hnau.commons.app.model.file.File
import org.hnau.commons.app.model.file.plus
import org.hnau.commons.app.model.preferences.impl.FileBasedPreferences
import org.hnau.commons.app.model.preferences.map
import org.hnau.commons.app.model.preferences.mapOption
import org.hnau.commons.app.model.preferences.withDefault
import org.hnau.commons.app.model.theme.ThemeBrightness
import org.hnau.commons.app.model.utils.Hue
import org.hnau.commons.kotlin.mapper.Mapper
import org.hnau.commons.kotlin.mapper.nameToEnum
import org.hnau.commons.kotlin.mapper.nullable
import org.hnau.commons.kotlin.mapper.plus
import org.hnau.commons.kotlin.mapper.stringToBoolean
import org.hnau.commons.kotlin.mapper.stringToInt

internal suspend fun AppContext(
    scope: CoroutineScope,
    defaultBrightness: ThemeBrightness?,
    defaultTryUseSystemHue: Boolean,
    fallbackHue: Hue,
    filesDir: File,
): AppContext {
    val preferences = FileBasedPreferences
        .Factory(
            preferencesFile = filesDir + "common_preferences.txt"
        )
        .createPreferences(
            scope = scope,
        )
    return AppContext(
        brightness = preferences["brightness"]
            .mapOption(
                scope = scope,
                mapper = Mapper
                    .nameToEnum<ThemeBrightness>()
                    .nullable
                    .let { mapper ->
                        Mapper(
                            direct = { nameOrNone ->
                                nameOrNone
                                    .map(mapper.direct)
                                    .getOrElse { defaultBrightness }
                            },
                            reverse = { brightnessOrNull ->
                                brightnessOrNull
                                    ?.let(mapper.reverse)
                                    .toOption()
                            },
                        )
                    },
            ),
        tryUseSystemHue = preferences["try_use_system_hue"]
            .map(
                scope = scope,
                mapper = Mapper.stringToBoolean,
            )
            .withDefault(
                scope = scope
            ) { defaultTryUseSystemHue },
        fallbackHue = preferences["fallback_hue"]
            .map(
                scope = scope,
                mapper = Mapper.stringToInt + Hue.intMapper,
            )
            .withDefault(
                scope = scope
            ) { fallbackHue },
        filesDir = filesDir,
    )
}