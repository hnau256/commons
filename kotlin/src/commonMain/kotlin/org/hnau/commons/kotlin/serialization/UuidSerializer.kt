package org.hnau.commons.kotlin.serialization

import kotlinx.serialization.builtins.serializer
import org.hnau.commons.kotlin.mapper.Mapper
import org.hnau.commons.kotlin.mapper.stringToUuid
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
object UuidSerializer: MappingKSerializer<String, Uuid>(
    base = String.serializer(),
    mapper = Mapper.stringToUuid,
)