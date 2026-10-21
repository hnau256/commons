package org.hnau.commons.kotlin.serialization

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateRange
import org.hnau.commons.kotlin.mapper.Mapper

data object LocalDateRangeSerializer : MappingKSerializer<ClosedRange<LocalDate>, LocalDateRange>(
    base = ClosedRangeSerializer(LocalDate.serializer()),
    mapper = Mapper(
        direct = { range ->
            LocalDateRange(
                start = range.start,
                endInclusive = range.endInclusive
            )
        },
        reverse = { range -> range }
    )
)