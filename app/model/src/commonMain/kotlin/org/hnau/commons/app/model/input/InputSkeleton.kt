@file:UseSerializers(
    OptionSerializer::class,
)

package org.hnau.commons.app.model.input

import arrow.core.Option
import arrow.core.serialization.OptionSerializer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class InputSkeleton<S, V>(
    val initialValue: Option<V>,
    val state: MutableStateFlow<S>,
)