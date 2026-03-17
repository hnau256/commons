package org.hnau.commons.app.projector.utils.theme

import androidx.compose.runtime.Immutable
import org.hnau.commons.app.projector.dynamiccolor.dynamiccolor.ColorSpec
import org.hnau.commons.app.projector.dynamiccolor.dynamiccolor.DynamicScheme
import org.hnau.commons.app.projector.dynamiccolor.dynamiccolor.Variant
import org.hnau.commons.app.model.theme.ThemeBrightnessValues

@Immutable
data class DynamicSchemeConfig(
    val variant: Variant = Variant.TONAL_SPOT,
    val contrastLevel: Double = 1.0,
    val specVersion: ColorSpec.SpecVersion = ColorSpec.SpecVersion.SPEC_2025,
    val platform: DynamicScheme.Platform = DynamicScheme.Platform.PHONE,
    val chroma: Double = 48.0,
    val tone: ThemeBrightnessValues<Double> = ThemeBrightnessValues(
        light = 40.0,
        dark = 80.0,
    )
) {

    companion object {

        val default: DynamicSchemeConfig = DynamicSchemeConfig()
    }
}