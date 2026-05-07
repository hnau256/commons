package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toolingGraphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import org.hnau.commons.app.model.theme.color.Contrast
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.context.color
import org.hnau.commons.app.projector.fractal.context.newTone
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.content
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.app.projector.utils.rememberRun

@Composable
fun FIcon(
    drawable: Drawable,
    modifier: Modifier = Modifier,
) {
    UpdateFContext(
        update = { newTone(Contrast.content) },
    ) {
        drawable.fold(
            ifPainter = { painter ->
                PainterIcon(
                    painter = painter,
                    modifier = modifier,
                )
            },
            ifVector = { vector ->
                PainterIcon(
                    painter = rememberVectorPainter(vector),
                    modifier = modifier,
                )
            },
            ifText = { text ->
                TextIcon(
                    text = text,
                    modifier = modifier,
                )
            }
        )
    }
}

@Composable
private fun PainterIcon(
    painter: Painter,
    modifier: Modifier = Modifier,
) {
    val fContext = LocalFContext.current
    Box(
        modifier = modifier
            .size(fContext.distance.units.iconSize)
            .toolingGraphicsLayer()
            .paint(
                painter = painter,
                colorFilter = ColorFilter.tint(
                    color = fContext.color,
                )
            )
    )
}

@Composable
private fun TextIcon(
    text: String,
    modifier: Modifier = Modifier,
) {
    val units = LocalFContext.current.distance.units
    Box(
        modifier = modifier
            .size(units.iconSize)
            .background(
                color = LocalFContext.current.color,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        UpdateFContext(
            update = { newTone(Contrast.content) },
        ) {
            BasicText(
                text = text.rememberRun { extractNChars(2) },
                maxLines = 1,
                minLines = 1,
                style = units.textStyle.small.merge(
                    color = LocalFContext.current.color,
                )
            )
        }
    }
}

private fun String.extractNChars(
    n: Int,
): String = this
    .split(' ')
    .filter(String::isNotEmpty)
    .let { words ->
        words
            .foldIndexed("" to n) { index, (acc, rem), word ->

                val left = words.size - index

                val limit = (rem + left - 1) / left

                val taken = word
                    .take(limit)
                    .replaceFirstChar(Char::uppercaseChar)

                (acc + taken) to (rem - taken.length)
            }
            .first
    }