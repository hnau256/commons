package org.hnau.commons.app.projector.fractal.utils

import org.hnau.commons.gen.enumvalues.annotations.EnumValues
import org.hnau.commons.gen.fold.annotations.Fold
import org.hnau.commons.kotlin.foldBoolean

@Fold
@EnumValues
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