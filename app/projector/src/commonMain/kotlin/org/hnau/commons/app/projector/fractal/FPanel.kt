package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.hnau.commons.app.model.theme.color.Contrast
import org.hnau.commons.app.model.theme.palette.PaletteType
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.context.color
import org.hnau.commons.app.projector.fractal.context.newTone
import org.hnau.commons.app.projector.fractal.size.fPadding
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.container
import org.hnau.commons.app.projector.fractal.utils.offset

@Composable
fun FPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    UpdateFContext(
        update = {
            copy(
                distance = distance.offset(1),
                palette = PaletteType.Neutral,
                customTone = null,
            )
        }
    ) {
        val fContext = LocalFContext.current
        Box(
            modifier = modifier
                .background(
                    color = fContext.color,
                    shape = fContext.distance.units.shape,
                )
                .fPadding(),
            propagateMinConstraints = true,
        ) {
            content()
        }
    }
}