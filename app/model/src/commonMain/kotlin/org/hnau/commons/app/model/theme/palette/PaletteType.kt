package org.hnau.commons.app.model.theme.palette

import org.hnau.commons.gen.enumvalues.annotations.EnumValues

@EnumValues
enum class PaletteType {
    Primary, Secondary, Tertiary,
    Neutral, NeutralVariant, Error, ;

    companion object {

        @Deprecated("Use Mood and Saturation instead")
        val default: PaletteType
            get() = Primary
    }
}