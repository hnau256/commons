package org.hnau.commons.app.projector.uikit.backbutton

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.hnau.commons.app.model.goback.GoBackHandler
import org.hnau.commons.app.projector.uikit.TopBarAction
import org.hnau.commons.app.projector.uikit.TopBarDefaults
import org.hnau.commons.app.projector.utils.Icon
import org.hnau.commons.kotlin.foldNullable

@Composable
fun BackButtonHost(
    contentPadding: PaddingValues,
    goBackHandler: GoBackHandler,
    content: @Composable (PaddingValues) -> Unit,
) {
    val goBackState: State<(() -> Unit)?> = goBackHandler.collectAsState()

    val targetWidth: Dp by remember {
        derivedStateOf {
            goBackState.value.foldNullable(
                ifNotNull = { TopBarDefaults.separation + TopBarDefaults.height },
                ifNull = { 0.dp },
            )
        }
    }

    val width: Dp by animateDpAsState(
        targetValue = targetWidth,
    )

    CompositionLocalProvider(
        LocalBackButtonWidth provides width
    ) {
        content(contentPadding)
    }

    TopBarAction(
        modifier = Modifier.offset(
            x = width - TopBarDefaults.height +
                    contentPadding.calculateLeftPadding(LocalLayoutDirection.current),
            y = contentPadding.calculateTopPadding(),
        ),
        onClick = { goBackHandler.value?.invoke() },
    ) {
        Icon(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
        )
    }
}