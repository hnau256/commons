package org.hnau.commons.app.projector.fractal.padding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier

@Composable
internal fun LocalContentPaddingBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .padding(LocalContentPadding.current),
        propagateMinConstraints = true,
    ) {
        CompositionLocalProvider(
            value = LocalContentPadding provides PaddingValues.Zero,
        ) {
            content()
        }
    }
}