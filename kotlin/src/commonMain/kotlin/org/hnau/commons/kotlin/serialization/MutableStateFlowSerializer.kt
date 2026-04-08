package org.hnau.commons.kotlin.serialization

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer
import org.hnau.commons.kotlin.mapper.Mapper

class MutableStateFlowSerializer<T>(
    valueSerializer: KSerializer<T>,
) : MappingKSerializer<T, MutableStateFlow<T>>(
    base = valueSerializer,
    mapper = Mapper(
        direct = { value -> MutableStateFlow(value) },
        reverse = { flow -> flow.value },
    ),
)
