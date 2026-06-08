package org.hnau.commons.app.projector.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

context(orientation: Orientation)
val PaddingValues.alongFrom: Dp
    @Composable
    get() = orientation.fold(
        ifHorizontal = { start },
        ifVertical = { top }
    )

context(orientation: Orientation)
val PaddingValues.alongTo: Dp
    @Composable
    get() = orientation.fold(
        ifHorizontal = { end },
        ifVertical = { bottom }
    )

context(orientation: Orientation)
val PaddingValues.acrossFrom: Dp
    @Composable
    get() = orientation.fold(
        ifHorizontal = { top },
        ifVertical = { start }
    )

context(orientation: Orientation)
val PaddingValues.acrossTo: Dp
    @Composable
    get() = orientation.fold(
        ifHorizontal = { bottom },
        ifVertical = { end }
    )

context(orientation: Orientation)
fun PaddingValues(
    alongFrom: Dp = 0.dp,
    alongTo: Dp = 0.dp,
    acrossFrom: Dp = 0.dp,
    acrossTo: Dp = 0.dp,
): PaddingValues = orientation.fold(
    ifHorizontal = {
        PaddingValues(
            start = alongFrom,
            top = acrossFrom,
            end = alongTo,
            bottom = acrossTo,
        )
    },
    ifVertical = {
        PaddingValues(
            start = acrossFrom,
            top = alongFrom,
            end = acrossTo,
            bottom = alongTo,
        )
    }
)