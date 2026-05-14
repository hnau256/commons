package org.hnau.commons.app.projector.fractal.semantic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.semantic.utils.LocalSContentPadding
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.uikit.TopBarDefaults
import org.hnau.commons.app.projector.uikit.backbutton.LocalBackButtonWidth
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.PaddingValuesZero
import org.hnau.commons.app.projector.utils.copy
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
        val additionalPadding = LocalFContext.current.distance.units.paddingValues.vertical.medium.copy(top = 0.dp)
        CompositionLocalProvider(
            LocalSContentPadding provides (contentPadding + additionalPadding)
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
                                bottom = LocalFContext.current.distance.units.padding.across.medium,
                            )
                    ) {
                        SActionsScope.actions()
                        Spacer(
                            modifier = Modifier.height(
                                TopBarDefaults.height + LocalSContentPadding.current.run {
                                    calculateTopPadding() + calculateBottomPadding()
                                }
                            )
                        )
                    }
                },
                content = {
                    CompositionLocalProvider(
                        value = LocalSContentPadding provides (LocalSContentPadding.current),
                        content = content,
                    )
                },
            )
        }
    }
}