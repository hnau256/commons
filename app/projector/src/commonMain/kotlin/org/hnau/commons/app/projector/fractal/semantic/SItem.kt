package org.hnau.commons.app.projector.fractal.semantic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            other = LocalFContext.current.distance.units.paddingValues.small,
        ) { actual, min ->
            actual.coerceAtLeast(min)
        },
        shapeToClip = null,
        startAccessory = startAccessory?.withoutSContentPaddings(),
        topAccessory = topAccessory?.withoutSContentPaddings(),
        endAccessory = endAccessory?.withoutSContentPaddings(),
        bottomAccessory = bottomAccessory?.withoutSContentPaddings(),
        onClick = onClick,
        content = content.withoutSContentPaddings(),
    )
}

private fun (@Composable (() -> Unit)).withoutSContentPaddings(): @Composable () -> Unit = {
    CompositionLocalProvider(
        value = LocalSContentPadding provides PaddingValues.Zero,
        content = this,
    )
}