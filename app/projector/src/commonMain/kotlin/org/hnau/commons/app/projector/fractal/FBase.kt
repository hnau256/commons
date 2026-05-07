package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.context.FContext
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.color
import org.hnau.commons.app.projector.fractal.size.fPadding

@Composable
fun FBase(
    context: FContext,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalFContext provides context,
    ) {
        val fContext = LocalFContext.current
        Box(
            modifier = modifier
                .background(
                    color = fContext.color,
                )
                .fPadding(),
        ) {
            content()
        }
    }
}