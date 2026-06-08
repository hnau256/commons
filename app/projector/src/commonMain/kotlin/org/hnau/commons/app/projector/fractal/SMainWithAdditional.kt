package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.padding.LocalContentPadding
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.utils.plus

@Composable
fun SMainWithAdditional(
    modifier: Modifier = Modifier,
    main: @Composable () -> Unit,
    additional: @Composable () -> Unit,
) {
    SOvercompose(
        modifier = modifier,
        top = {
            CompositionLocalProvider(
                value = LocalContentPadding provides LocalContentPadding.current + PaddingValues(
                    bottom = LocalDistance.current.units.padding.along.medium,
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    propagateMinConstraints = true,
                ) {
                    additional()
                }
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            propagateMinConstraints = true,
        ) {
            main()
        }
    }
}