package org.hnau.commons.app.projector.fractal.utils

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val Distance.cornerRadius: Dp
    get() = 8.dp.scale(spaceScale)


val localCornerRadius: Dp
    @Composable
    get() = Distance.local.cornerRadius

val Distance.shape: Shape
    get() = RoundedCornerShape(size = cornerRadius)

val localShape: Shape
    @Composable
    get() = RoundedCornerShape(size = localCornerRadius)