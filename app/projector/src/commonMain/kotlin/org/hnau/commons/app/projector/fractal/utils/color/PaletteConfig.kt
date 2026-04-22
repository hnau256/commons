package org.hnau.commons.app.projector.fractal.utils.color

import org.hnau.commons.app.projector.dynamiccolor.dynamiccolor.DynamicScheme
import org.hnau.commons.app.projector.dynamiccolor.dynamiccolor.Variant

data class PaletteConfig(
    val variant: Variant,
    val contrastLevel: Double,
    val platform: DynamicScheme.Platform,
    val chroma: Double,
) {

    companion object {

        val default = PaletteConfig(
            variant = Variant.TONAL_SPOT,
            contrastLevel = 1.0,
            platform = DynamicScheme.Platform.PHONE,
            chroma = 48.0,
        )
    }
}