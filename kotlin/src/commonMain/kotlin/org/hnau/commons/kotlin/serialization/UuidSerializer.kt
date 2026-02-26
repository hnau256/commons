package org.hnau.commons.kotlin.serialization

import org.hnau.commons.kotlin.mapper.Mapper
import org.hnau.commons.kotlin.mapper.stringToUuid
import kotlinx.serialization.builtins.serializer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
object UuidSerializer: MappingKSerializer<String, Uuid>(
    base = String.serializer(),
    mapper = Mapper.stringToUuid,
)