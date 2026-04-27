package org.hnau.commons.app.model.theme.palette

import org.hnau.commons.app.model.color.dynamic.dynamiccolor.ColorSpec
import org.hnau.commons.app.model.color.dynamic.dynamiccolor.DynamicScheme
import org.hnau.commons.app.model.color.dynamic.dynamiccolor.Variant
import org.hnau.commons.app.model.theme.color.Chroma
import org.hnau.commons.app.model.theme.color.Contrast

data class PalettesGenerateConfig(
    val variant: Variant,
    val chroma: Chroma,
    val platform: DynamicScheme.Platform,
    val contrast: Contrast,
    val spec: ColorSpec.SpecVersion,
) {

    companion object {

        val default: PalettesGenerateConfig = PalettesGenerateConfig(
            variant = Variant.TONAL_SPOT,
            chroma = Chroma.create(48),
            platform = DynamicScheme.Platform.PHONE,
            contrast = Contrast.min,
            spec = ColorSpec.SpecVersion.SPEC_2026,
        )
    }
}