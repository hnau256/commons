package hnau.commons.kotlin.serialization

import hnau.commons.kotlin.mapper.Mapper
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

class ClosedRangeSerializer<T : Comparable<T>>(
    itemSerializer: KSerializer<T>,
) : MappingKSerializer<ClosedRangeSerializer.Surrogate<T>, ClosedRange<T>>(
    base = Surrogate.serializer(itemSerializer),
    mapper = Mapper(
        direct = { surrogate -> surrogate.from..surrogate.to },
        reverse = { range ->
            Surrogate(
                from = range.start,
                to = range.endInclusive,
            )
        }
    )
) {

    @Serializable
    data class Surrogate<T>(
        val from: T,
        val to: T
    )
}