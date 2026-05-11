@file:UseSerializers(
    MutableStateFlowSerializer::class,
)

package org.hnau.commons.app.model.input.skeleton

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.hnau.commons.kotlin.coroutines.flow.state.mutable.toMutableStateFlowAsInitial
import org.hnau.commons.kotlin.serialization.MutableStateFlowSerializer

@Serializable
data class InputSkeleton<S>(
    val initialValue: S,
    val state: MutableStateFlow<S> = initialValue.toMutableStateFlowAsInitial(),
)

