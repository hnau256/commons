package org.hnau.commons.app.projector.fractal.utils.color

import org.hnau.commons.app.model.theme.PaletteType
import org.hnau.commons.app.model.theme.ThemeBrightness
import org.hnau.commons.app.model.color.dynamic.dynamiccolor.ColorSpec
import org.hnau.commons.app.model.color.dynamic.hct.Hct
import org.hnau.commons.app.model.color.dynamic.palettes.TonalPalette

fun ColorSpec.getPalette(
    mainHtc: Hct,
    type: PaletteType,
    config: PaletteConfig,
    brightness: ThemeBrightness,
): TonalPalette {

    val isDark: Boolean = when (brightness) {
        ThemeBrightness.Light -> false
        ThemeBrightness.Dark -> true
    }

    return when (type) {
        PaletteType.Primary -> getPrimaryPalette(
            /* variant = */ config.variant,
            /* sourceColorHct = */ mainHtc,
            /* isDark = */ isDark,
            /* platform = */ config.platform,
            /* contrastLevel = */ config.contrastLevel
        )

        PaletteType.Secondary -> getSecondaryPalette(
            /* variant = */ config.variant,
            /* sourceColorHct = */ mainHtc,
            /* isDark = */ isDark,
            /* platform = */ config.platform,
            /* contrastLevel = */ config.contrastLevel
        )

        PaletteType.Tertiary -> getTertiaryPalette(
            /* variant = */ config.variant,
            /* sourceColorHct = */ mainHtc,
            /* isDark = */ isDark,
            /* platform = */ config.platform,
            /* contrastLevel = */ config.contrastLevel
        )

        PaletteType.Neutral -> getNeutralPalette(
            /* variant = */ config.variant,
            /* sourceColorHct = */ mainHtc,
            /* isDark = */ isDark,
            /* platform = */ config.platform,
            /* contrastLevel = */ config.contrastLevel
        )

        PaletteType.NeutralVariant -> getNeutralVariantPalette(
            /* variant = */ config.variant,
            /* sourceColorHct = */ mainHtc,
            /* isDark = */ isDark,
            /* platform = */ config.platform,
            /* contrastLevel = */ config.contrastLevel
        )

        PaletteType.Error -> getErrorPalette(
            /* variant = */ config.variant,
            /* sourceColorHct = */ mainHtc,
            /* isDark = */ isDark,
            /* platform = */ config.platform,
            /* contrastLevel = */ config.contrastLevel
        )
    }
}