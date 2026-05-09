package org.hnau.commons.app.projector.fractal.utils

import org.hnau.commons.app.model.theme.palette.PaletteType
import org.hnau.commons.kotlin.foldBoolean

@Deprecated("Use Mood and Saturation instead")
fun PaletteType.orInactive(
    active: Boolean,
): PaletteType = active.foldBoolean(
    ifTrue = { this },
    ifFalse = { PaletteType.Neutral }
)

@Deprecated("Use Mood and Saturation instead")
fun PaletteType.orError(
    error: Boolean,
): PaletteType = error.foldBoolean(
    ifTrue = { PaletteType.Error },
    ifFalse = { this }
)

fun PaletteType.Companion.resolve(
    mood: Mood,
    saturation: Saturation,
): PaletteType = saturation.fold(
    ifNeutral = { PaletteType.Neutral },
    ifActive = {
        mood.fold(
            ifPrimary = { PaletteType.Primary },
            ifSecondary = { PaletteType.Secondary },
            ifTertiary = { PaletteType.Tertiary },
            ifError = { PaletteType.Error }
        )
    }
)