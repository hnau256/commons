package org.hnau.commons.app.projector.fractal.utils

import org.hnau.commons.gen.enumvalues.annotations.EnumValues
import org.hnau.commons.gen.fold.annotations.Fold

@EnumValues
@Fold
enum class Importance {
    Primary, Secondary, Tertiary;

    companion object {

        val default: Importance
            get() = Primary
    }
}