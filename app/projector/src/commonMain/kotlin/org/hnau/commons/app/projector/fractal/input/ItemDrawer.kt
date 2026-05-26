package org.hnau.commons.app.projector.fractal.input

import androidx.compose.runtime.Composable
import org.hnau.commons.app.projector.uikit.table.TableScope

interface ItemDrawer {

    @Composable
    fun TableScope.Item(
        onClick: (() -> Unit)? = null,
        endAccessory: (@Composable () -> Unit)? = null,
        isFocused: Boolean = false,
        content: @Composable () -> Unit,
    )
}