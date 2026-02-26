package hnau.commons.kotlin.serialization

import hnau.commons.kotlin.mapper.Mapper
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateRange

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