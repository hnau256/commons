package org.hnau.commons.app.projector.utils.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import org.hnau.commons.app.model.color.dynamic.dynamiccolor.DynamicScheme
import org.hnau.commons.app.model.color.dynamic.palettes.TonalPalette
import org.hnau.commons.app.model.theme.ThemeBrightness
import org.hnau.commons.app.model.theme.color.Hue
import org.hnau.commons.app.model.theme.color.Tone
import org.hnau.commons.app.model.theme.fold
import org.hnau.commons.app.model.theme.isDark
import org.hnau.commons.app.model.theme.palette.PaletteType
import org.hnau.commons.app.model.theme.palette.Palettes
import org.hnau.commons.app.model.theme.palette.PalettesGenerateConfig
import org.hnau.commons.app.model.theme.palette.SystemPalettes
import org.hnau.commons.app.model.theme.palette.create
import org.hnau.commons.app.projector.fractal.utils.color.tone.getHct

fun Palettes.Companion.create(
    hue: Hue,
    brightness: ThemeBrightness,
    config: PalettesGenerateConfig,
): Palettes = Palettes(
    palettes = TonalPalette.create(
        hue = hue,
        brightness = brightness,
        config = config,
    ),
    config = config,
    brightness = brightness,
)

fun Palettes.Companion.createFromSystemOrFallback(
    fallbackHue: Hue,
    systemPalettes: SystemPalettes,
    brightness: ThemeBrightness,
    config: PalettesGenerateConfig,
): Palettes = when (systemPalettes) {
    is SystemPalettes.Some -> systemPalettes.palettes
    SystemPalettes.None -> Palettes.create(
        hue = fallbackHue,
        brightness = brightness,
        config = config,
    )
}

fun Palettes.toColorScheme(): ColorScheme = DynamicScheme(
    sourceColorHct = palettes[PaletteType.Primary].getHct(Tone.avg),
    variant = config.variant,
    isDark = brightness.isDark,
    contrastLevel = config.contrast.contrast.toDouble(),
    platform = config.platform,
    specVersion = config.spec,
    primaryPalette = palettes[PaletteType.Primary],
    secondaryPalette = palettes[PaletteType.Secondary],
    tertiaryPalette = palettes[PaletteType.Primary],
    neutralPalette = palettes[PaletteType.Primary],
    neutralVariantPalette = palettes[PaletteType.Primary],
    errorPalette = palettes[PaletteType.Primary],
).toColorScheme(
    brightness = brightness,
)

private fun DynamicScheme.toColorScheme(
    brightness: ThemeBrightness,
): ColorScheme {
    val primary = Color(primary)
    val onPrimary = Color(onPrimary)
    val secondary = Color(secondary)
    val onSecondary = Color(onSecondary)
    val tertiary = Color(tertiary)
    val onTertiary = Color(onTertiary)
    val primaryContainer = Color(primaryContainer)
    val onPrimaryContainer = Color(onPrimaryContainer)
    val secondaryContainer = Color(secondaryContainer)
    val onSecondaryContainer = Color(onSecondaryContainer)
    val tertiaryContainer = Color(tertiaryContainer)
    val onTertiaryContainer = Color(onTertiaryContainer)
    val surface = Color(surface)
    val onSurface = Color(onSurface)
    val surfaceVariant = Color(surfaceVariant)
    val onSurfaceVariant = Color(onSurfaceVariant)
    val surfaceContainerLowest = Color(surfaceContainerLowest)
    val surfaceContainerLow = Color(surfaceContainerLow)
    val surfaceContainer = Color(surfaceContainer)
    val surfaceContainerHigh = Color(surfaceContainerHigh)
    val surfaceContainerHighest = Color(surfaceContainerHighest)
    val surfaceBright = Color(surfaceBright)
    val surfaceDim = Color(surfaceDim)
    val surfaceTint = Color(surfaceTint)
    val inverseSurface = Color(inverseSurface)
    val inverseOnSurface = Color(inverseOnSurface)
    val inversePrimary = Color(inversePrimary)
    val background = Color(background)
    val onBackground = Color(onBackground)
    val error = Color(error)
    val onError = Color(onError)
    val outline = Color(outline)
    val outlineVariant = Color(outlineVariant)
    val scrim = Color(scrim)

    return brightness.fold(
        ifLight = {
            lightColorScheme(
                primary = primary,
                onPrimary = onPrimary,
                secondary = secondary,
                onSecondary = onSecondary,
                tertiary = tertiary,
                onTertiary = onTertiary,
                primaryContainer = primaryContainer,
                onPrimaryContainer = onPrimaryContainer,
                secondaryContainer = secondaryContainer,
                onSecondaryContainer = onSecondaryContainer,
                tertiaryContainer = tertiaryContainer,
                onTertiaryContainer = onTertiaryContainer,
                surface = surface,
                onSurface = onSurface,
                surfaceVariant = surfaceVariant,
                onSurfaceVariant = onSurfaceVariant,
                surfaceContainerLowest = surfaceContainerLowest,
                surfaceContainerLow = surfaceContainerLow,
                surfaceContainer = surfaceContainer,
                surfaceContainerHigh = surfaceContainerHigh,
                surfaceContainerHighest = surfaceContainerHighest,
                surfaceBright = surfaceBright,
                surfaceDim = surfaceDim,
                surfaceTint = surfaceTint,
                inverseSurface = inverseSurface,
                inverseOnSurface = inverseOnSurface,
                inversePrimary = inversePrimary,
                background = background,
                onBackground = onBackground,
                error = error,
                onError = onError,
                outline = outline,
                outlineVariant = outlineVariant,
                scrim = scrim,
            )
        },
        ifDark = {
            darkColorScheme(
                primary = primary,
                onPrimary = onPrimary,
                secondary = secondary,
                onSecondary = onSecondary,
                tertiary = tertiary,
                onTertiary = onTertiary,
                primaryContainer = primaryContainer,
                onPrimaryContainer = onPrimaryContainer,
                secondaryContainer = secondaryContainer,
                onSecondaryContainer = onSecondaryContainer,
                tertiaryContainer = tertiaryContainer,
                onTertiaryContainer = onTertiaryContainer,
                surface = surface,
                onSurface = onSurface,
                surfaceVariant = surfaceVariant,
                onSurfaceVariant = onSurfaceVariant,
                surfaceContainerLowest = surfaceContainerLowest,
                surfaceContainerLow = surfaceContainerLow,
                surfaceContainer = surfaceContainer,
                surfaceContainerHigh = surfaceContainerHigh,
                surfaceContainerHighest = surfaceContainerHighest,
                surfaceBright = surfaceBright,
                surfaceDim = surfaceDim,
                surfaceTint = surfaceTint,
                inverseSurface = inverseSurface,
                inverseOnSurface = inverseOnSurface,
                inversePrimary = inversePrimary,
                background = background,
                onBackground = onBackground,
                error = error,
                onError = onError,
                outline = outline,
                outlineVariant = outlineVariant,
                scrim = scrim,
            )
        }
    )
}