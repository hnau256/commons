package org.hnau.commons.app.projector.fractal.semantic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.semantic.utils.LocalSContentPadding
import org.hnau.commons.app.projector.fractal.size.SizeType
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.utils.PaddingValuesZero
import org.hnau.commons.app.projector.utils.plus

@Composable
fun SScreen(
    contentPadding: PaddingValues = PaddingValuesZero,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        propagateMinConstraints = true,
    ) {
        CompositionLocalProvider(
            LocalSContentPadding provides contentPadding + LocalFContext.current.distance.units.paddingValues[SizeType.default]
        ) {
            content()
        }
    }
}