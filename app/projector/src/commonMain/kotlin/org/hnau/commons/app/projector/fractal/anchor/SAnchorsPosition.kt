package org.hnau.commons.app.projector.fractal.anchor

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest
import org.hnau.commons.kotlin.foldBoolean

@Composable
internal fun rememberSAnchorsPosition(
    getPosition: () -> Position,
    getIsDragging: () -> Boolean,
): State<Position> {

    val result = remember(getPosition) {
        mutableStateOf(getPosition())
    }

    LaunchedEffect(getPosition, getIsDragging) {
        snapshotFlow {
            getPosition() to getIsDragging()
        }.collectLatest { (position, dragging) ->
            val target = position
            if (target == result.value) return@collectLatest
            dragging.foldBoolean(
                ifTrue = {
                    result.value = target
                },
                ifFalse = {
                    animate(
                        initialValue = result.value,
                        targetValue = target,
                        typeConverter = Position.twoWayConverter,
                        animationSpec = spring(),
                    ) { value, _ ->
                        result.value = value
                    }
                },
            )
        }
    }
    return result
}
