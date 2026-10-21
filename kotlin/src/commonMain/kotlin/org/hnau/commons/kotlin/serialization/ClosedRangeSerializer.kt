package org.hnau.commons.kotlin.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import org.hnau.commons.kotlin.mapper.Mapper

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