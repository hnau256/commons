package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.hnau.commons.app.projector.fractal.padding.LocalContentPadding
import org.hnau.commons.app.projector.uikit.line.weight
import org.hnau.commons.app.projector.utils.copy

@Composable
fun SMainWithAdditional(
    modifier: Modifier = Modifier,
    main: @Composable () -> Unit,
    additional: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        val contentPadding = LocalContentPadding.current
        CompositionLocalProvider(
            value = LocalContentPadding provides contentPadding.copy(
                bottom = 0.dp,
            )
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                propagateMinConstraints = true,
            ) {
                additional()
            }
        }
        CompositionLocalProvider(
            value = LocalContentPadding provides contentPadding.copy(
                top = 0.dp,
            )
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                propagateMinConstraints = true,
            ) {
                main()
            }
        }
    }
}