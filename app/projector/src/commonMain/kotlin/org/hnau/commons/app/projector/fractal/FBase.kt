package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.context.FContext
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.context.containerColor
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.fractal.utils.Saturation
import org.hnau.commons.app.projector.fractal.utils.plus

@Composable
fun FBase(
    context: FContext,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalFContext provides context.copy(
            distance = context.distance + (-1),
        ),
    ) {
        val fContext = LocalFContext.current
        Box(
            modifier = modifier
                .background(
                    color = fContext.containerColor,
                ),
        ) {
            UpdateFContext(
                update = {
                    copy(
                        distance = distance + 1,
                        saturation = Saturation.Neutral,
                        mood = Mood.default,
                        customContainerTone = null,
                    )
                }
            ) {
                content()
            }
        }
    }
}