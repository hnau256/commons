package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.context.containerColor
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.LocalSContentPadding
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.fractal.utils.Saturation
import org.hnau.commons.app.projector.fractal.utils.plus

@Composable
fun SPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val fContext = LocalFContext.current
    Box(
        propagateMinConstraints = true,
        modifier = modifier
            .padding(LocalSContentPadding.current)
            .background(
                color = fContext.containerColor,
                shape = fContext.distance.units.shape,
            )
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
            CompositionLocalProvider(
                LocalSContentPadding provides LocalFContext.current.distance.units.paddingValues.vertical.medium,
            ) {
                content()
            }
        }
    }
}