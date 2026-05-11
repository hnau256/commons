package org.hnau.commons.app.projector.fractal.semantic.input

import androidx.compose.runtime.Composable

interface ItemDrawer {

    @Composable
    fun Item(
        onClick: (() -> Unit)? = null,
        endAccessory: (@Composable () -> Unit)? = null,
        content: @Composable () -> Unit,
    )
}