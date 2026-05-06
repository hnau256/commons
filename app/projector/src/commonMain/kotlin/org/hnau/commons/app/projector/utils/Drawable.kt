package org.hnau.commons.app.projector.utils

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.painter.Painter as ComposePainter

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

inline fun <R> Drawable.fold(
    ifPainter: (ComposePainter) -> R,
    ifVector: (ImageVector) -> R,
    ifText: (String) -> R,
): R = when (this) {
    is Drawable.Painter -> ifPainter(painter)
    is Drawable.Vector -> ifVector(vector)
    is Drawable.Text -> ifText(text)
}