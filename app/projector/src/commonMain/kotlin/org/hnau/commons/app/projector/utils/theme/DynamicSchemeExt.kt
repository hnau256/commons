package org.hnau.commons.app.projector.utils.theme

import org.hnau.commons.app.projector.dynamiccolor.dynamiccolor.ColorSpecs
import org.hnau.commons.app.projector.dynamiccolor.dynamiccolor.DynamicScheme
import org.hnau.commons.app.projector.dynamiccolor.hct.Hct
import org.hnau.commons.app.model.utils.Hue
import org.hnau.commons.app.model.theme.ThemeBrightness

fun DynamicScheme(
    hue: Hue,
    brightness: ThemeBrightness,
    config: DynamicSchemeConfig = DynamicSchemeConfig.default,
): DynamicScheme {

    val primary = Hct.from(
        /* hue = */ hue.degrees.toDouble(),
        /* chroma = */ config.chroma,
        /* tone = */ config.tone[brightness],
    )

    val isDark = when (brightness) {
        ThemeBrightness.Light -> false
        ThemeBrightness.Dark -> true
    }

    val colorSpec = ColorSpecs.get(config.specVersion)
    return DynamicScheme(
        /* sourceColorHct = */ primary,
        /* variant = */  config.variant,
        /* isDark = */ isDark,
        /* contrastLevel = */  config.contrastLevel,
        /* platform = */ config.platform,
        /* specVersion = */ config.specVersion,
        /* primaryPalette = */ colorSpec.getPrimaryPalette(
            /* variant = */ config.variant,
            /* sourceColorHct = */ primary,
            /* isDark = */ isDark,
            /* platform = */ config.platform,
            /* contrastLevel = */ config.contrastLevel
        ),
        /* secondaryPalette = */ colorSpec.getSecondaryPalette(
            /* variant = */ config.variant,
            /* sourceColorHct = */ primary,
            /* isDark = */ isDark,
            /* platform = */ config.platform,
            /* contrastLevel = */ config.contrastLevel
        ),
        /* tertiaryPalette = */ colorSpec.getTertiaryPalette(
            /* variant = */ config.variant,
            /* sourceColorHct = */ primary,
            /* isDark = */ isDark,
            /* platform = */ config.platform,
            /* contrastLevel = */ config.contrastLevel
        ),
        /* neutralPalette = */ colorSpec.getNeutralPalette(
            /* variant = */ config.variant,
            /* sourceColorHct = */ primary,
            /* isDark = */ isDark,
            /* platform = */ config.platform,
            /* contrastLevel = */ config.contrastLevel
        ),
        /* neutralVariantPalette = */ colorSpec.getNeutralVariantPalette(
            /* variant = */ config.variant,
            /* sourceColorHct = */ primary,
            /* isDark = */ isDark,
            /* platform = */ config.platform,
            /* contrastLevel = */ config.contrastLevel
        ),
        /* errorPalette = */ colorSpec.getErrorPalette(
            /* variant = */ config.variant,
            /* sourceColorHct = */ primary,
            /* isDark = */ isDark,
            /* platform = */ config.platform,
            /* contrastLevel = */ config.contrastLevel
        )
    )
}