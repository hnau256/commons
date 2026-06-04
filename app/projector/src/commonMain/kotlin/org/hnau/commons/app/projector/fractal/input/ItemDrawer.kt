package org.hnau.commons.app.projector.fractal.input

import androidx.compose.runtime.Composable
import org.hnau.commons.app.projector.fractal.table.SCellScope
import org.hnau.commons.app.projector.fractal.table.STableScope

interface ItemDrawer {

    @Composable
    fun SCellScope.Item(
        onClick: (() -> Unit)? = null,
        endAccessory: (@Composable () -> Unit)? = null,
        isFocused: Boolean = false,
        content: @Composable () -> Unit,
    )
}