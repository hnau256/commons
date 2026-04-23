package org.hnau.commons.app.projector.fractal.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val Distance.borderWidth: Dp
    get() = 2.dp.scale(
        scale = contentScale,
        step = 1.dp,
    )


val localBorderWidth: Dp
    @Composable
    get() = Distance.local.borderWidth