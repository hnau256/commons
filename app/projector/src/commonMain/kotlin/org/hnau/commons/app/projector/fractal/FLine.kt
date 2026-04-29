package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.utils.size.FUnits

@Composable
fun FRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val units = FUnits.local
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(units.padding.horizontal.medium),
        content = content
    )
}

@Composable
fun FColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val units = FUnits.local
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(units.padding.vertical.medium),
        content = content
    )
}