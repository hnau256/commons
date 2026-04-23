package org.hnau.commons.app.projector.fractal.utils

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.hnau.commons.app.projector.utils.DeflatedRoundedCornerShape

val Distance.cornerRadius: Dp
    get() = 12.dp.scale(spaceScale)


val localCornerRadius: Dp
    @Composable
    get() = Distance.local.cornerRadius

val Distance.shape: Shape
    get() = RoundedCornerShape(size = cornerRadius)

val localShape: Shape
    @Composable
    get() = Distance.local.shape

val Distance.borderShape: Shape
    get() = DeflatedRoundedCornerShape(
        topStart = CornerSize(cornerRadius),
        deflation = borderWidth / 2,
    )

val localBorderShape: Shape
    @Composable
    get() = Distance.local.borderShape