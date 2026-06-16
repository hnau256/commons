package org.hnau.commons.app.projector.fractal.padding

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.color
import org.hnau.commons.app.projector.utils.bottom
import org.hnau.commons.app.projector.utils.left
import org.hnau.commons.app.projector.utils.right
import org.hnau.commons.app.projector.utils.top

@Composable
fun Modifier.localContentPaddingShadow(): Modifier {
    val contentPadding = LocalContentPadding.current
    val left = with(LocalDensity.current) { contentPadding.left.toPx() }
    val top = with(LocalDensity.current) { contentPadding.top.toPx() }
    val right = with(LocalDensity.current) { contentPadding.right.toPx() }
    val bottom = with(LocalDensity.current) { contentPadding.bottom.toPx() }
    val color = LocalFContext.current.color.copy(alpha = 0.95f)
    return drawWithContent {
        drawContent()
        if (top > 0) {
            drawRect(
                color = color,
                topLeft = Offset.Zero,
                size = size.copy(
                    height = top,
                ),
            )
        }
        if (bottom > 0) {
            drawRect(
                color = color,
                topLeft = Offset(
                    x = 0f,
                    y = size.height - bottom,
                ),
                size = size.copy(
                    height = bottom,
                ),
            )
        }
        if (left > 0) {
            drawRect(
                color = color,
                topLeft = Offset(
                    x = 0f,
                    y = top,
                ),
                size = Size(
                    width = left,
                    height = size.height - top - bottom,
                ),
            )
        }
        if (right > 0) {
            drawRect(
                color = color,
                topLeft = Offset(
                    x = size.width - right,
                    y = top,
                ),
                size = Size(
                    width = right,
                    height = size.height - top - bottom,
                ),
            )
        }
    }
}