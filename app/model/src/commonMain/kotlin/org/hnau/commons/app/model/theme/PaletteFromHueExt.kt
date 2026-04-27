package org.hnau.commons.app.model.theme

import org.hnau.commons.app.model.color.dynamic.dynamiccolor.ColorSpec
import org.hnau.commons.app.model.color.dynamic.dynamiccolor.ColorSpec2026
import org.hnau.commons.app.model.color.dynamic.dynamiccolor.DynamicScheme
import org.hnau.commons.app.model.color.dynamic.dynamiccolor.Variant
import org.hnau.commons.app.model.color.dynamic.hct.Hct

fun Palette.Companion.create(
    hue: Hue,
    variant: Variant,
    brightness: ThemeBrightness,
    chroma: Chroma = Chroma.default,
    platform: DynamicScheme.Platform = DynamicScheme.Platform.PHONE,
    contrast: Contrast = Contrast.min,
    spec: ColorSpec = ColorSpec2026(),
): PaletteTypeValues<Palette> = PaletteTypeValues.create { type ->
    create(
        hue = hue,
        type = type,
        variant = variant,
        brightness = brightness,
        chroma = chroma,
        platform = platform,
        contrast = contrast,
        spec = spec,
    )
}

fun Palette.Companion.create(
    hue: Hue,
    type: PaletteType,
    variant: Variant,
    brightness: ThemeBrightness,
    chroma: Chroma = Chroma.default,
    platform: DynamicScheme.Platform = DynamicScheme.Platform.PHONE,
    contrast: Contrast = Contrast.min,
    spec: ColorSpec = ColorSpec2026(),
): Palette {

    val isDark: Boolean = when (brightness) {
        ThemeBrightness.Light -> false
        ThemeBrightness.Dark -> true
    }

    val mainHtc = Hct.from(
        hue = hue.degrees.toDouble(),
        chroma = chroma.raw.toDouble(),
        tone = Tone.avg.raw.toDouble(),
    )

    val contrastLevel = contrast
        .contrast
        .toDouble()

    val tonelPalette = when (type) {
        PaletteType.Primary -> spec.getPrimaryPalette(
            /* variant = */ variant,
            /* sourceColorHct = */ mainHtc,
            /* isDark = */ isDark,
            /* platform = */ platform,
            /* contrastLevel = */ contrastLevel
        )

        PaletteType.Secondary -> spec.getSecondaryPalette(
            /* variant = */ variant,
            /* sourceColorHct = */ mainHtc,
            /* isDark = */ isDark,
            /* platform = */ platform,
            /* contrastLevel = */ contrastLevel
        )

        PaletteType.Tertiary -> spec.getTertiaryPalette(
            /* variant = */ variant,
            /* sourceColorHct = */ mainHtc,
            /* isDark = */ isDark,
            /* platform = */ platform,
            /* contrastLevel = */ contrastLevel
        )

        PaletteType.Neutral -> spec.getNeutralPalette(
            /* variant = */ variant,
            /* sourceColorHct = */ mainHtc,
            /* isDark = */ isDark,
            /* platform = */ platform,
            /* contrastLevel = */ contrastLevel
        )

        PaletteType.NeutralVariant -> spec.getNeutralVariantPalette(
            /* variant = */ variant,
            /* sourceColorHct = */ mainHtc,
            /* isDark = */ isDark,
            /* platform = */ platform,
            /* contrastLevel = */ contrastLevel
        )

        PaletteType.Error -> spec.getErrorPalette(
            /* variant = */ variant,
            /* sourceColorHct = */ mainHtc,
            /* isDark = */ isDark,
            /* platform = */ platform,
            /* contrastLevel = */ contrastLevel
        )

    }

    return Palette { tone ->
        tonelPalette.getHct(
            tone = tone.raw,
        )
    }
}