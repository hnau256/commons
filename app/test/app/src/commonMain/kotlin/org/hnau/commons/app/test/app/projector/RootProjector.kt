package org.hnau.commons.app.test.app.projector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.app.projector.fractal.SAnchors
import org.hnau.commons.app.projector.fractal.SText
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.test.app.model.RootModel
import org.hnau.commons.gen.pipe.annotations.Pipe
import org.hnau.commons.kotlin.coroutines.flow.state.mapWithScope
import org.hnau.commons.kotlin.ifTrue
import org.hnau.commons.kotlin.map

class RootProjector(
    scope: CoroutineScope,
    dependencies: Dependencies,
    private val model: RootModel,
) {

    @Pipe
    interface Dependencies {

        fun stack(): RootStackProjector.Dependencies

        companion object
    }

    private val stack = model
        .stack
        .mapWithScope(scope) { scope, stackModelOrLoading ->
            stackModelOrLoading.map { stackModel ->
                RootStackProjector(
                    scope = scope,
                    model = stackModel,
                    dependencies = dependencies.stack(),
                )
            }
        }

    @Composable
    fun Content(
        contentPadding: PaddingValues,
    ) {
        var position by remember { mutableFloatStateOf(0f) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(LocalDistance.current.units.paddingValues.vertical.medium),
            verticalArrangement = Arrangement.spacedBy(LocalDistance.current.units.padding.along.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            remember {
                listOf<Pair<NonEmptyList<Float>, (@Composable (Int) -> Unit)?>>(
                    Pair(
                        first = nonEmptyListOf(1f),
                        second = null,
                    ),
                    Pair(
                        first = nonEmptyListOf(1f, 2f),
                        second = {
                            SText(
                                text = "A".repeat(it + 1),
                            )
                        },
                    ),
                )
            }.forEach { (weights, item) ->
                var position by remember { mutableFloatStateOf(0f) }
                remember { listOf(true, false) }.forEach { enabled ->
                    SAnchors(
                        modifier = Modifier.fillMaxWidth(),
                        orientation = Orientation.Horizontal,
                        weights = weights,
                        snap = item != null,
                        getPosition = { position },
                        onPositionChanged = enabled.ifTrue { { position = it } },
                        item = item,
                    )
                }
            }
        }
        /*BackButtonHost(
            contentPadding = contentPadding,
            goBackHandler = model.goBackHandler,
            backButtonBackgroundColor = LocalFContext.current.containerOverlay().color,
        ) { contentPadding ->
            stack
                .collectAsState()
                .value
                .LoadableContent(
                    transitionSpec = TransitionSpec.rememberCrossfade(),
                ) { stack ->
                    stack.Content(
                        contentPadding = contentPadding
                    )
                }
        }*/
    }
}