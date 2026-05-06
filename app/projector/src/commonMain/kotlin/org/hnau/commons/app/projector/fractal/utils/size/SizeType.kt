package org.hnau.commons.app.projector.fractal.utils.size

import org.hnau.commons.gen.enumvalues.annotations.EnumValues

@EnumValues
enum class SizeType {
    ExtraSmall, Small, Medium, Large;

    companion object {

        val default: SizeType
            get() = Medium
    }
}