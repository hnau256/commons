package org.hnau.commons.app.projector.fractal.semantic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.hnau.commons.app.projector.fractal.FLine
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.semantic.utils.LocalSContentPadding
import org.hnau.commons.app.projector.fractal.size.SizeType
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.plus
import org.hnau.commons.app.projector.uikit.TopBarDefaults
import org.hnau.commons.app.projector.uikit.backbutton.LocalBackButtonWidth
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.PaddingValuesZero
import org.hnau.commons.app.projector.utils.plus

@Composable
fun SScreen(
    contentPadding: PaddingValues = PaddingValuesZero,
    actions: @Composable SActionsScope.() -> Unit = {},
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        propagateMinConstraints = true,
    ) {
        CompositionLocalProvider( //TODO use paddingValues only for content
            LocalSContentPadding provides contentPadding + LocalFContext.current.distance.units.paddingValues[SizeType.default]
        ) {
            SOvercompose(
                top = {
                    SLine(
                        orientation = Orientation.Horizontal,
                        alignment = Alignment.End,
                        reverseOrdering = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = LocalBackButtonWidth.current,
                                bottom = LocalFContext.current.distance.units.padding.vertical.medium,
                            )
                    ) {
                        UpdateFContext(
                            update = {
                                copy(
                                    distance = distance + 1,
                                )
                            }
                        ) {
                            SActionsScope.actions()
                        }
                        Spacer(
                            modifier = Modifier.height(
                                TopBarDefaults.height + LocalSContentPadding.current.run {
                                    calculateTopPadding() + calculateBottomPadding()
                                }
                            )
                        )
                    }
                },
                content = content,
            )
        }
    }
}