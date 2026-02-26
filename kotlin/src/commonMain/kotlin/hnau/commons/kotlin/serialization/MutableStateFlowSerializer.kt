package hnau.commons.kotlin.serialization

import hnau.commons.kotlin.mapper.Mapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer

class MutableStateFlowSerializer<T>(
    valueSerializer: KSerializer<T>,
) : MappingKSerializer<T, MutableStateFlow<T>>(
    base = valueSerializer,
    mapper = Mapper(
        direct = { value -> MutableStateFlow(value) },
        reverse = { flow -> flow.value },
    ),
)
