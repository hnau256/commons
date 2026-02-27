package hnau.common.app.projector.utils.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import hnau.common.app.model.utils.Hue
import hnau.common.app.model.theme.ThemeBrightness

fun buildColorScheme(
    hue: Hue,
    brightness: ThemeBrightness,
    config: DynamicSchemeConfig = DynamicSchemeConfig.default,
): ColorScheme {

    val scheme = DynamicScheme(
        hue = hue,
        brightness = brightness,
        config = config,
    )

    val primary = Color(scheme.primary)
    val onPrimary = Color(scheme.onPrimary)
    val secondary = Color(scheme.secondary)
    val onSecondary = Color(scheme.onSecondary)
    val tertiary = Color(scheme.tertiary)
    val onTertiary = Color(scheme.onTertiary)
    val primaryContainer = Color(scheme.primaryContainer)
    val onPrimaryContainer = Color(scheme.onPrimaryContainer)
    val secondaryContainer = Color(scheme.secondaryContainer)
    val onSecondaryContainer = Color(scheme.onSecondaryContainer)
    val tertiaryContainer = Color(scheme.tertiaryContainer)
    val onTertiaryContainer = Color(scheme.onTertiaryContainer)
    val surface = Color(scheme.surface)
    val onSurface = Color(scheme.onSurface)
    val surfaceVariant = Color(scheme.surfaceVariant)
    val onSurfaceVariant = Color(scheme.onSurfaceVariant)
    val surfaceContainerLowest = Color(scheme.surfaceContainerLowest)
    val surfaceContainerLow = Color(scheme.surfaceContainerLow)
    val surfaceContainer = Color(scheme.surfaceContainer)
    val surfaceContainerHigh = Color(scheme.surfaceContainerHigh)
    val surfaceContainerHighest = Color(scheme.surfaceContainerHighest)
    val surfaceBright = Color(scheme.surfaceBright)
    val surfaceDim = Color(scheme.surfaceDim)
    val surfaceTint = Color(scheme.surfaceTint)
    val inverseSurface = Color(scheme.inverseSurface)
    val inverseOnSurface = Color(scheme.inverseOnSurface)
    val inversePrimary = Color(scheme.inversePrimary)
    val background = Color(scheme.background)
    val onBackground = Color(scheme.onBackground)
    val error = Color(scheme.error)
    val onError = Color(scheme.onError)
    val outline = Color(scheme.outline)
    val outlineVariant = Color(scheme.outlineVariant)
    val scrim = Color(scheme.scrim)

    return when (brightness) {
        ThemeBrightness.Dark -> darkColorScheme(
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

        ThemeBrightness.Light -> lightColorScheme(
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
}