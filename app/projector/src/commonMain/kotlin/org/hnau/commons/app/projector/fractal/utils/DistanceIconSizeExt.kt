package org.hnau.commons.app.projector.fractal.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val Distance.iconSize: Dp
    get() = 24.dp.scale(contentScale)


val localIconSize: Dp
    @Composable
    get() = Distance.local.iconSize