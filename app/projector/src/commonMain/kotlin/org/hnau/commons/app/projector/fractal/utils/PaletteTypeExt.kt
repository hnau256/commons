package org.hnau.commons.app.projector.fractal.utils

import org.hnau.commons.app.model.theme.palette.PaletteType
import org.hnau.commons.kotlin.foldBoolean

fun PaletteType.orInactive(
    active: Boolean,
): PaletteType = active.foldBoolean(
    ifTrue = { this },
    ifFalse = { PaletteType.Neutral }
)