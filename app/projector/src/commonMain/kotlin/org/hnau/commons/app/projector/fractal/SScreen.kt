package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.context.FContext
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.color
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.padding.LocalContentPadding
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.uikit.TopBarDefaults
import org.hnau.commons.app.projector.uikit.backbutton.LocalBackButtonWidth
import org.hnau.commons.app.projector.uikit.line.weight
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.bottom
import org.hnau.commons.app.projector.utils.copy
import org.hnau.commons.app.projector.utils.plus
import org.hnau.commons.app.projector.utils.top

@Composable
fun SScreen(
    contentPadding: PaddingValues,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable STableActionsScope.() -> Unit = {},
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        value = LocalContentPadding provides contentPadding + LocalDistance.current.units.paddingValues.vertical.medium.copy(
            top = TopBarDefaults.separationTop
        ),
    ) {
        val fContext = LocalFContext.current
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(fContext.color),
            propagateMinConstraints = true,
        ) {
            SOvercompose(
                top = {
                    val contentPadding = LocalContentPadding.current
                    SLine(
                        orientation = Orientation.Horizontal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = LocalBackButtonWidth.current,
                                bottom = LocalDistance.current.units.padding.across.medium,
                            )
                            .height(
                                TopBarDefaults.height +
                                        contentPadding.top +
                                        contentPadding.bottom,
                            ),
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
}
