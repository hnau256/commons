package org.hnau.commons.app.projector.fractal.utils

import org.hnau.commons.kotlin.foldBoolean

enum class Saturation {
    Active, Neutral;

    companion object {

        val default: Saturation
            get() = Neutral

        fun get(
            active: Boolean,
        ): Saturation = active.foldBoolean(
            ifTrue = { Active },
            ifFalse = { Neutral },
        )
    }
}

inline fun <R> Saturation.fold(
    ifActive: () -> R,
    ifNeutral: () -> R,
): R = when (this) {
    Saturation.Active -> ifActive()
    Saturation.Neutral -> ifNeutral()
}