package org.hnau.commons.app.model.theme

import org.hnau.commons.gen.enumvalues.annotations.EnumValues

@EnumValues
enum class PaletteType {
    Primary, Secondary, Tertiary,
    Neutral, NeutralVariant, Error,;

    companion object
}