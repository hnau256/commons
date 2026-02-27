package hnau.common.app.projector.uikit.backbutton

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import hnau.common.app.model.goback.GoBackHandler
import hnau.common.app.projector.uikit.TopBarAction
import hnau.common.app.projector.uikit.TopBarDefaults
import hnau.common.app.projector.utils.Icon
import hnau.common.kotlin.coroutines.flow.state.mapState
import hnau.common.kotlin.foldNullable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

class BackButtonProjector(
    scope: CoroutineScope,
    private val goBackHandler: GoBackHandler,
) {

    private val targetWidth: StateFlow<Dp> = goBackHandler.mapState(scope) { goBackHandler ->
        goBackHandler.foldNullable(
            ifNotNull = { maxWidth },
            ifNull = { 0.dp },
        )
    }

    private val widthAnimation: Animatable<Dp, AnimationVector1D> = Animatable(
        initialValue = targetWidth.value,
        typeConverter = Dp.Companion.VectorConverter,
        visibilityThreshold = Dp.Companion.VisibilityThreshold,
    )

    val width: Dp
        get() = widthAnimation.value

    @Composable
    fun Content() {
        val insets = WindowInsets.systemBars.asPaddingValues()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets),
            contentAlignment = Alignment.TopStart,
        ) {
            TopBarAction(
                modifier = Modifier
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