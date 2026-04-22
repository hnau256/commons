package org.hnau.commons.app.projector.fractal.utils.color

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.Importance
import org.hnau.commons.app.projector.fractal.utils.local

@Composable
fun FractalColorsProvider.getBackgroundColor(): Color = getBackgroundColor(
    distance = Distance.local,
)

@Composable
fun FractalColorsProvider.getComponentColors(
    importance: Importance,
): ComponentValues<Color> = getComponentColors(
    distanceWithImportance = DistanceWithImportance(
        distance = Distance.local,
        importance = importance,
    )
)