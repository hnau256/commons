package org.hnau.commons.app.projector.utils

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.painter.Painter as ComposePainter
import org.hnau.commons.gen.fold.annotations.Fold

@Fold
sealed interface Drawable {

    data class Painter(
        val painter: ComposePainter,
    ) : Drawable

    data class Vector(
        val vector: ImageVector,
    ) : Drawable

    data class Text(
        val text: String,
    ): Drawable
}