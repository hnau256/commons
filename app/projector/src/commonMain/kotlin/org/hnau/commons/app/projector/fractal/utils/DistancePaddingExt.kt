package org.hnau.commons.app.projector.fractal.utils

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val Distance.paddingHorizontal: Dp
    get() = 16.dp.scale(spaceScale)

val Distance.paddingVertical: Dp
    get() = 8.dp.scale(spaceScale)


val localPaddingHorizontal: Dp
    @Composable
    get() = Distance.local.paddingHorizontal

val localPaddingVertical: Dp
    @Composable
    get() = Distance.local.paddingVertical

@Composable
fun Modifier.padding(): Modifier = padding(
    horizontal = localPaddingHorizontal,
    vertical = localPaddingVertical,
)