package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.hnau.commons.app.model.theme.color.Contrast
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.FContext
import org.hnau.commons.app.projector.fractal.context.color
import org.hnau.commons.app.projector.fractal.context.overlay
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.fractal.utils.Saturation
import org.hnau.commons.app.projector.fractal.utils.content
import org.hnau.commons.app.projector.uikit.TopBarDefaults
import org.hnau.commons.app.projector.uikit.backbutton.LocalBackButtonWidth
import org.hnau.commons.app.projector.uikit.line.Line
import org.hnau.commons.app.projector.uikit.line.weight
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.Overcompose
import org.hnau.commons.app.projector.utils.copy
import org.hnau.commons.app.projector.utils.plus

@Composable
fun SScreen(
    contentPadding: PaddingValues,
    title: @Composable () -> Unit,
    actions: @Composable STableActionsScope.() -> Unit = {},
    content: @Composable (contentPadding: PaddingValues) -> Unit,
) {
    val fContext = LocalFContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(fContext.color),
        propagateMinConstraints = true,
    ) {
        Overcompose(
            contentPadding = contentPadding + LocalDistance.current.units.paddingValues.vertical.medium.copy(
                top = TopBarDefaults.separationTop
            ),
            top = { contentPadding ->
                Line(
                    orientation = Orientation.Horizontal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(contentPadding)
                        .padding(
                            start = LocalBackButtonWidth.current,
                            bottom = LocalDistance.current.units.padding.across.medium,
                        )
                        .height(TopBarDefaults.height),
                ) {
                    FContext(
                        update = {
                            copy(
                                mood = Mood.Active.default,
                            )
                        }
                    ) {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            title()
                        }
                    }
                    SActions(
                        orientation = Orientation.Horizontal,
                        actions = actions,
                    )
                }
            },
            content = content,
        )
    }
}
