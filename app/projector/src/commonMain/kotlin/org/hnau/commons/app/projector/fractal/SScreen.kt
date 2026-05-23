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
import androidx.compose.ui.unit.dp
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.context.containerColor
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.fractal.utils.Saturation
import org.hnau.commons.app.projector.uikit.TopBarDefaults
import org.hnau.commons.app.projector.uikit.backbutton.LocalBackButtonWidth
import org.hnau.commons.app.projector.uikit.line.weight
import org.hnau.commons.app.projector.uikit.table.Table
import org.hnau.commons.app.projector.uikit.table.TableScope
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.copy
import org.hnau.commons.app.projector.utils.plus

@Composable
fun SScreen(
    contentPadding: PaddingValues,
    title: @Composable () -> Unit,
    actions: @Composable TableScope.() -> Unit = {},
    content: @Composable () -> Unit,
) {
    val fContext = LocalFContext.current
    val additionalPadding = fContext.distance.units.paddingValues.vertical.medium.copy(top = 0.dp)
    val fullPadding = contentPadding + additionalPadding
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(fContext.containerColor),
        propagateMinConstraints = true,
    ) {
        SOvercompose(
            modifier = Modifier.padding(fullPadding),
            top = {
                Table(
                    orientation = Orientation.Horizontal,
                    reverseOrdering = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = TopBarDefaults.separationTop,
                            start = LocalBackButtonWidth.current,
                            bottom = LocalFContext.current.distance.units.padding.across.medium,
                        )
                        .height(TopBarDefaults.height),
                ) {
                    actions()
                    SCellBox(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        UpdateFContext(
                            update = {
                                copy(
                                    mood = Mood.Primary,
                                    saturation = Saturation.Active,
                                )
                            }
                        ) {
                            title()
                        }
                    }
                }
            },
            content = content,
        )
    }
}
