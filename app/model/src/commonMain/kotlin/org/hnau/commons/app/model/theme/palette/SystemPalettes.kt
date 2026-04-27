package org.hnau.commons.app.model.theme.palette

sealed interface SystemPalettes {

    data object None : SystemPalettes

    data class Some(
        val palettes: PaletteTypeValues<Palette>,
    ) : SystemPalettes

    companion object
}