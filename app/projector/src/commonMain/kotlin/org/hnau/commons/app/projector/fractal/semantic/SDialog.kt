package org.hnau.commons.app.projector.fractal.semantic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.model.theme.ThemeBrightnessValues
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.semantic.utils.LocalSContentPadding
import org.hnau.commons.app.projector.fractal.utils.BaseWithDecay
import org.hnau.commons.app.projector.fractal.utils.float
import org.hnau.commons.app.projector.uikit.state.NullableStateContent
import org.hnau.commons.app.projector.uikit.transition.TransitionSpec

data class DialogContentInfo(
    val content: @Composable () -> Unit,
    val actions: @Composable SActionsScope.() -> Unit,
    val cancel: (() -> Unit)?,
)

@Composable
fun SDialog(
    info: StateFlow<DialogContentInfo?>,
) {
    info
        .collectAsState()
        .value
        .NullableStateContent(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = TransitionSpec.rememberCrossfade(),
        ) { info ->
            val fContext = LocalFContext.current
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clickable(
                        onClick = { info.cancel?.invoke() },
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    )
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(
                            alpha = shadowAlpha[fContext.palettes.brightness][fContext.distance],
                        )
                    )
                    .padding(LocalSContentPadding.current)
            ) {
                CompositionLocalProvider(
                    LocalSContentPadding provides PaddingValues.Zero,
                ) {
                    SPanel {
                        SContentWithActions(
                            content = info.content,
                            actions = info.actions
                        )
                    }
                }
            }
        }
}


private val shadowAlpha: ThemeBrightnessValues<BaseWithDecay<Float>> =
    ThemeBrightnessValues(
        dark = BaseWithDecay.float(
            initial = 0.75f,
            decay = 0.5,
        ),
        light = BaseWithDecay.float(
            initial = 0.25f,
            decay = 0.75,
        ),
    )