package org.hnau.commons.app.projector.fractal.semantic

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.FPanel
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.semantic.utils.LocalSContentPadding
import org.hnau.commons.app.projector.fractal.size.SizeType
import org.hnau.commons.app.projector.fractal.size.units

@Composable
fun SPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    FPanel(
        modifier = modifier.padding(LocalSContentPadding.current),
        contentPadding = PaddingValues.Zero,
    ) {
        CompositionLocalProvider(
            LocalSContentPadding provides LocalFContext.current.distance.units.paddingValues[SizeType.default],
        ) {
            content()
        }
    }
}