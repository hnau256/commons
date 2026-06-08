package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.hnau.commons.app.projector.fractal.padding.LocalContentPadding
import org.hnau.commons.app.projector.uikit.line.Line
import org.hnau.commons.app.projector.uikit.line.LineScope
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.PaddingValues
import org.hnau.commons.app.projector.utils.acrossFrom
import org.hnau.commons.app.projector.utils.acrossTo
import org.hnau.commons.app.projector.utils.alongFrom
import org.hnau.commons.app.projector.utils.alongTo

@Composable
fun SLine(
    orientation: Orientation,
    modifier: Modifier = Modifier,
    separation: Dp = 0.dp,
    reverseOrdering: Boolean = false,
    content: @Composable LineScope.() -> Unit,
) {
    with(orientation) {
        val contentPadding = LocalContentPadding.current
        Line(
            modifier = modifier.padding(
                PaddingValues(
                    alongFrom = contentPadding.alongFrom,
                    alongTo = contentPadding.alongTo,
                )
            ),
            orientation = orientation,
            separation = separation,
            reverseOrdering = reverseOrdering,
        ) {
            CompositionLocalProvider(
                value = LocalContentPadding provides PaddingValues(
                    acrossFrom = contentPadding.acrossFrom,
                    acrossTo = contentPadding.acrossTo,
                )
            ) {
                content()
            }
        }
    }
}