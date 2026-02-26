package hnau.commons.kotlin.mapper

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun Mapper.Companion.bytesToString(): Mapper<ByteArray, String> = Mapper(
    reverse = { it.encodeToByteArray() },
    direct = { it.decodeToString() },
)

private val bytesToStringInner = Mapper.bytesToString()
val Mapper.Companion.bytesToString: Mapper<ByteArray, String> get() = bytesToStringInner

private val stringToByteInner = Mapper(String::toByte, Byte::toString)
val Mapper.Companion.stringToByte get() = stringToByteInner

private val stringToShortInner = Mapper(String::toShort, Short::toString)
val Mapper.Companion.stringToShort get() = stringToShortInner

private val stringToIntInner = Mapper(String::toInt, Int::toString)
val Mapper.Companion.stringToInt get() = stringToIntInner

private val stringToLongInner = Mapper(String::toLong, Long::toString)
val Mapper.Companion.stringToLong get() = stringToLongInner

private val stringToFloatInner = Mapper(String::toFloat, Float::toString)
val Mapper.Companion.stringToFloat get() = stringToFloatInner

private val stringToDoubleInner = Mapper(String::toDouble, Double::toString)
val Mapper.Companion.stringToDouble get() = stringToDoubleInner

private val stringToBooleanInner = Mapper(String::toBoolean, Boolean::toString)
val Mapper.Companion.stringToBoolean get() = stringToBooleanInner

@OptIn(ExperimentalUuidApi::class)
private val stringToUuidInner = Mapper(Uuid.Companion::parse, Uuid::toString)

@OptIn(ExperimentalUuidApi::class)
val Mapper.Companion.stringToUuid get() = stringToUuidInner

private const val stringToOptionNonePrefix = '0'
private const val stringToOptionSomePrefix = '1'
private val stringToOptionInner = Mapper<String, Option<String>>(
    direct = { string ->
        when (string.firstOrNull()) {
            stringToOptionSomePrefix -> Some(string.drop(1))
            else -> None
        }
    },
    reverse = { value ->
        when (value) {
            None -> stringToOptionNonePrefix.toString()
            is Some -> stringToOptionSomePrefix + value.value
        }
    },
)
val Mapper.Companion.stringToOption get() = stringToOptionInner

fun Mapper.Companion.stringSplit(
    separator: Char,
): Mapper<String, List<String>> = Mapper(
    direct = { string -> string.split(separator) },
    reverse = { strings -> strings.joinToString(separator = separator.toString()) },
)
