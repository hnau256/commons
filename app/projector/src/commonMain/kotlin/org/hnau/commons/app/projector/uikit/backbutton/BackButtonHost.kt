package org.hnau.commons.app.projector.uikit.backbutton

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import org.hnau.commons.app.model.goback.GoBackHandler
import org.hnau.commons.app.projector.uikit.TopBarAction
import org.hnau.commons.app.projector.uikit.TopBarDefaults
import org.hnau.commons.app.projector.utils.Icon
import org.hnau.commons.kotlin.coroutines.flow.state.mapState
import org.hnau.commons.kotlin.foldNullable

@Composable
fun BackButtonHost(
    contentPadding: PaddingValues,
    goBackHandler: GoBackHandler,
    content: @Composable (PaddingValues) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val targetWidth by remember(goBackHandler) {
        goBackHandler.mapState(scope) { goBackHandler ->
            goBackHandler.foldNullable(
                ifNotNull = { TopBarDefaults.separation + TopBarDefaults.height },
                ifNull = { 0.dp },
            )
        }
    }.collectAsState()

    val width by animateDpAsState(
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