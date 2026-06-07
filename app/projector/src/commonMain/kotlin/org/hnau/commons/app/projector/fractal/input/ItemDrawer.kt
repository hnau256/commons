package org.hnau.commons.app.projector.fractal.input

import androidx.compose.runtime.Composable

interface ItemDrawer {

    @Composable
    fun Item(
        onClick: (() -> Unit)? = null,
        endAccessory: (@Composable () -> Unit)? = null,
        isFocused: Boolean = false,
        content: @Composable () -> Unit,
    )
}