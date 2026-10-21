package org.hnau.commons.app.projector.fractal.table

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.padding.LocalContentPadding
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.utils.plus

@Composable
fun STableHeader(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier,
        propagateMinConstraints = true,
    ) {
        val padding = LocalDistance.current
            .units
            .padding
        val additionalPadding = PaddingValues(
            top = padding.along.medium,
            start = padding.across.small,
            end = padding.across.small,
            bottom = padding.along.extraSmall,
        )
        CompositionLocalProvider(
            LocalContentPadding provides LocalContentPadding.current + additionalPadding,
        ) {
            content()
        }
    }
}