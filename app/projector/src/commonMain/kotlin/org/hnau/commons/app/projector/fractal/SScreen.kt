package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.uikit.TopBarDefaults
import org.hnau.commons.app.projector.uikit.backbutton.LocalBackButtonWidth
import org.hnau.commons.app.projector.uikit.line.Line
import org.hnau.commons.app.projector.uikit.line.weight
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.copy
import org.hnau.commons.app.projector.utils.plus

@Composable
fun SScreen(
    contentPadding: PaddingValues,
    actions: @Composable SActionsScope.() -> Unit = {},
    content: @Composable () -> Unit,
) {
    val additionalPadding = LocalFContext.current.distance.units.paddingValues.vertical.medium.copy(top = 0.dp)
    val fullPadding = contentPadding + additionalPadding
    Box(
        modifier = Modifier.fillMaxSize(),
        propagateMinConstraints = true,
    ) {
        SOvercompose(
            modifier = Modifier.padding(fullPadding),
            top = {
                Line(
                    orientation = Orientation.Horizontal,
                    reverseOrdering = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = LocalBackButtonWidth.current,
                            bottom = LocalFContext.current.distance.units.padding.across.medium,
                        )
                ) {
                    Spacer(Modifier.weight(1f))
                    SActionsScope.actions()
                    Spacer(
                        modifier = Modifier.height(TopBarDefaults.height)
                    )
                }
            },
            content = content,
        )
    }
}
