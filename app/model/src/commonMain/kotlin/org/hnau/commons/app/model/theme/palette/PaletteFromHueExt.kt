package org.hnau.commons.app.model.theme.palette

import org.hnau.commons.app.model.color.dynamic.dynamiccolor.ColorSpec
import org.hnau.commons.app.model.color.dynamic.dynamiccolor.ColorSpec2021
import org.hnau.commons.app.model.color.dynamic.dynamiccolor.ColorSpec2026
import org.hnau.commons.app.model.color.dynamic.hct.Hct
import org.hnau.commons.app.model.color.dynamic.palettes.TonalPalette
import org.hnau.commons.app.model.theme.ThemeBrightness
import org.hnau.commons.app.model.theme.color.Hue
import org.hnau.commons.app.model.theme.color.Tone
import org.hnau.commons.app.model.theme.isDark

fun TonalPalette.Companion.createAll(
    hue: Hue,
    brightness: ThemeBrightness,
    config: PalettesGenerateConfig = PalettesGenerateConfig.default,
): PaletteTypeValues<TonalPalette> = PaletteTypeValues.create { type ->
    create(
        hue = hue,
        type = type,
        brightness = brightness,
        config = config,
    )
}

fun TonalPalette.Companion.create(
    hue: Hue,
    type: PaletteType,
    brightness: ThemeBrightness,
    config: PalettesGenerateConfig = PalettesGenerateConfig.default,
): TonalPalette {

    val isDark: Boolean = brightness.isDark

    val mainHtc = Hct.from(
        hue = hue.degrees.toDouble(),
        chroma = config.chroma.raw.toDouble(),
        tone = Tone.avg.raw.toDouble(),
    )

    val contrastLevel = config
        .contrast
        .contrast
        .toDouble()
    
    val spec = when (config.spec) {
        ColorSpec.SpecVersion.SPEC_2021 -> ColorSpec2021()
        ColorSpec.SpecVersion.SPEC_2025 -> ColorSpec2026()
        ColorSpec.SpecVersion.SPEC_2026 -> ColorSpec2026()
    }

    return when (type) {
        PaletteType.Primary -> spec.getPrimaryPalette(
            /* variant = */ config.variant,
            /* sourceColorHct = */ mainHtc,
            /* isDark = */ isDark,
            /* platform = */ config.platform,
            /* contrastLevel = */ contrastLevel
        )

        PaletteType.Secondary -> spec.getSecondaryPalette(
            /* variant = */ config.variant,
            /* sourceColorHct = */ mainHtc,
            /* isDark = */ isDark,
            /* platform = */ config.platform,
            /* contrastLevel = */ contrastLevel
        )

        PaletteType.Tertiary -> spec.getTertiaryPalette(
            /* variant = */ config.variant,
            /* sourceColorHct = */ mainHtc,
            /* isDark = */ isDark,
            /* platform = */ config.platform,
            /* contrastLevel = */ contrastLevel
        )

        PaletteType.Neutral -> spec.getNeutralPalette(
            /* variant = */ config.variant,
            /* sourceColorHct = */ mainHtc,
            /* isDark = */ isDark,
            /* platform = */ config.platform,
            /* contrastLevel = */ contrastLevel
        )

        PaletteType.NeutralVariant -> spec.getNeutralVariantPalette(
            /* variant = */ config.variant,
            /* sourceColorHct = */ mainHtc,
            /* isDark = */ isDark,
            /* platform = */ config.platform,
            /* contrastLevel = */ contrastLevel
        )

        PaletteType.Error -> spec.getErrorPalette(
            /* variant = */ config.variant,
            /* sourceColorHct = */ mainHtc,
            /* isDark = */ isDark,
            /* platform = */ config.platform,
            /* contrastLevel = */ contrastLevel
        )
    }
}