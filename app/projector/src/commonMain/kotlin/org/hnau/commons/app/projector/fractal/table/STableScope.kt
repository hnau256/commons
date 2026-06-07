package org.hnau.commons.app.projector.fractal.table

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.uikit.line.LineScope
import org.hnau.commons.app.projector.utils.Orientation

interface STableScope : LineScope {

    val orientation: Orientation

    @Composable
    fun SCell(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit,
    )

    companion object
}