package org.hnau.commons.app.projector.fractal.utils

import org.hnau.commons.app.model.theme.palette.PaletteType

fun PaletteType.Companion.resolve(
    mood: Mood,
    saturation: Saturation,
): PaletteType = saturation.fold(
    ifNeutral = {
        mood.fold(
            ifPrimary = { PaletteType.Neutral },
            ifSecondary = { PaletteType.Neutral },
            ifTertiary = { PaletteType.Neutral },
            ifError = { PaletteType.Error }
        )
    },
    ifActive = {
        mood.fold(
            ifPrimary = { PaletteType.Primary },
            ifSecondary = { PaletteType.Secondary },
            ifTertiary = { PaletteType.Tertiary },
            ifError = { PaletteType.Error }
        )
    }
)