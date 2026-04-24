package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.utils.localUnits

@Composable
fun FRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val units = localUnits
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(units.paddingHorizontal),
        content = content
    )
}

@Composable
fun FColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val units = localUnits
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(units.paddingVertical),
        content = content
    )
}