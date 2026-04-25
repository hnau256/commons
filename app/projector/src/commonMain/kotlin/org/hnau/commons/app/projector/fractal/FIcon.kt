package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toolingGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import org.hnau.commons.app.projector.fractal.utils.color.localContent
import org.hnau.commons.app.projector.fractal.utils.size.FUnits

@Composable
fun FIcon(
    painter: Painter,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .toolingGraphicsLayer()
            .size(FUnits.local.iconSize)
            .paint(
                painter = painter,
                colorFilter = ColorFilter.tint(
                    color = Color.localContent,
                )
            )
    )
}

@Composable
fun FIcon(
    image: ImageVector,
    modifier: Modifier = Modifier,
) {
    FIcon(
        painter = rememberVectorPainter(image),
        modifier = modifier,
    )
}