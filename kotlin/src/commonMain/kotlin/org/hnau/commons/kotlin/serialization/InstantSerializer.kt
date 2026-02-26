package org.hnau.commons.kotlin.serialization

import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.serializers.FormattedInstantSerializer

data object InstantSerializer: FormattedInstantSerializer(
    name = "hnau.commons.kotlin.serialization.InstantSerializer",
    format = DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET,
)