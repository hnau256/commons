package org.hnau.commons.app.projector.fractal.utils.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import org.hnau.commons.app.model.theme.ThemeBrightness
import org.hnau.commons.app.model.theme.color.Hue
import org.hnau.commons.app.model.theme.palette.Palettes
import org.hnau.commons.app.model.theme.palette.PalettesGenerateConfig
import org.hnau.commons.app.model.theme.palette.SystemPalettes
import org.hnau.commons.app.projector.fractal.FBase
import org.hnau.commons.app.projector.fractal.FColumn
import org.hnau.commons.app.projector.fractal.FPanel
import org.hnau.commons.app.projector.utils.theme.create

@Composable
fun FractalPreview(
    content: @Composable () -> Unit,
) {
    Row {
        ThemeBrightness.entries.forEach { themeBrightness ->
            Column {
                (0 until 5).forEach { hueIndex ->
                    FBase(
                        palettes = Palettes.create(
                            fallbackHue = Hue(hueIndex * 72),
                            systemPalettes = SystemPalettes.None,
                            brightness = themeBrightness,
                            config = PalettesGenerateConfig.default,
                        ),
                    ) {
                        RecursivePreview(
                            nextLevels = 3,
                            content = content,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecursivePreview(
    nextLevels: Int,
    content: @Composable () -> Unit,
) {
    if (nextLevels <= 0) {
        content()
        return
    }
    FColumn {
        FPanel {
            RecursivePreview(
                nextLevels = nextLevels - 1,
                content = content,
            )
        }
        content()
    }
}