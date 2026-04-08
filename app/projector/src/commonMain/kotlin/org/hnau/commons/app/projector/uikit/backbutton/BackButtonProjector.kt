package org.hnau.commons.app.projector.uikit.backbutton

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.model.goback.GoBackHandler
import org.hnau.commons.app.projector.uikit.TopBarAction
import org.hnau.commons.app.projector.uikit.TopBarDefaults
import org.hnau.commons.app.projector.utils.Icon
import org.hnau.commons.kotlin.coroutines.flow.state.mapState
import org.hnau.commons.kotlin.foldNullable

class BackButtonProjector(
    scope: CoroutineScope,
    private val goBackHandler: GoBackHandler,
) {
    private val targetWidth: StateFlow<Dp> =
        goBackHandler.mapState(scope) { goBackHandler ->
            goBackHandler.foldNullable(
                ifNotNull = { maxWidth },
                ifNull = { 0.dp },
            )
        }

    private val widthAnimation: Animatable<Dp, AnimationVector1D> =
        Animatable(
            initialValue = targetWidth.value,
            typeConverter = Dp.Companion.VectorConverter,
            visibilityThreshold = Dp.Companion.VisibilityThreshold,
        )

    val width: Dp
        get() = widthAnimation.value

    @Composable
    fun Content(
        contentPadding: PaddingValues,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.TopStart,
        ) {
            TopBarAction(
                modifier =
                    Modifier
                        .offset(x = width - buttonSize),
                onClick = { goBackHandler.value?.invoke() },
            ) {
                Icon(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                )
            }
        }
        LaunchedEffect(Unit) {
            targetWidth.collect { targetWidth ->
                widthAnimation.animateTo(
                    targetValue = targetWidth,
                )
            }
        }
    }

    companion object {
        private val buttonSize: Dp
            get() = TopBarDefaults.height

        private val maxWidth: Dp =
            TopBarDefaults.separation + buttonSize
    }
}
