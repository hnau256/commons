package org.hnau.commons.app.projector.fractal.semantic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.coerceAtLeast
import org.hnau.commons.app.projector.fractal.FItem
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.semantic.utils.LocalSContentPadding
import org.hnau.commons.app.projector.fractal.size.SizeType
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.utils.combineWith

@Composable
fun SItem(
    modifier: Modifier = Modifier,
    startAccessory: @Composable (() -> Unit)? = null,
    topAccessory: @Composable (() -> Unit)? = null,
    endAccessory: @Composable (() -> Unit)? = null,
    bottomAccessory: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    FItem(
        modifier = modifier,
        contentPadding = LocalSContentPadding.current.combineWith(
            other = LocalFContext.current.distance.units.paddingValues[SizeType.default],
        ) { actual, min ->
            actual.coerceAtLeast(min)
        },
        shapeToClip = null,
        startAccessory = startAccessory,
        topAccessory = topAccessory,
        endAccessory = endAccessory,
        bottomAccessory = bottomAccessory,
        onClick = onClick,
        content = content,
    )
}