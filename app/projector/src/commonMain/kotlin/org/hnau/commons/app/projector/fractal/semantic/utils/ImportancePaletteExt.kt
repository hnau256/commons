package org.hnau.commons.app.projector.fractal.semantic.utils

import org.hnau.commons.app.model.theme.palette.PaletteType

val Importance.palette: PaletteType
    get() = fold(
        ifPrimary = { PaletteType.Primary },
        ifSecondary = { PaletteType.Secondary },
        ifTertiary = { PaletteType.Tertiary },
        ifQuaternary = { PaletteType.Neutral },
    )