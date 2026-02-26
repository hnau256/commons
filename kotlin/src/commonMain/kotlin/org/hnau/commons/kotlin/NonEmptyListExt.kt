package hnau.commons.kotlin

import arrow.core.NonEmptyList
import arrow.core.toNonEmptyListOrThrow

fun <T : Comparable<T>> NonEmptyList<T>.findMinMax(): ClosedRange<T> {
    var min = head
    var max = head
    tail.forEach { item ->
        when {
            item < min -> min = item
            item > max -> max = item
        }
    }
    return min..max
}

inline fun <I, K, O> NonEmptyList<I>.groupByToNonEmptyList(
    keySelector: (I) -> KeyValue<K, O>,
): NonEmptyList<KeyValue<K, NonEmptyList<O>>> = groupByToNonEmpty(keySelector)
    .map { entry -> entry.toKeyValue() }
    .toNonEmptyListOrThrow()