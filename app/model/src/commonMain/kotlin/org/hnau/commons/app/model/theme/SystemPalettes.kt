package org.hnau.commons.app.model.theme

sealed interface SystemPalettes {

    data object None : SystemPalettes

    data class Some(
        val palettes: SystemPalettes,
    ) : SystemPalettes

    companion object
}