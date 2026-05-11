package org.hnau.commons.app.projector.fractal.semantic.input

import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.app.model.input.InputModel
import org.hnau.commons.app.model.input.InputType

data class SInputProjectorFactory<S, E, V, I : InputType<S, E, V>>(
    val scope: CoroutineScope,
    val model: InputModel<S, E, V, I>,
    val contentProjector: SInputContentProjector,
)

inline fun <S, E, V, I : InputType<S, E, V>> InputModel<S, E, V, I>.toSInputProjectorFactory(
    scope: CoroutineScope,
    createContentProjector: InputModel<S, E, V, I>.(scope: CoroutineScope) -> SInputContentProjector
): SInputProjectorFactory<S, E, V, I> = SInputProjectorFactory(
    scope = scope,
    contentProjector = createContentProjector(scope),
    model = this,
)