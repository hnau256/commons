package org.hnau.commons.kotlin

import arrow.core.NonEmptyList
import arrow.core.toNonEmptyListOrThrow

inline fun <I, O> NonEmptyList<I>.foldNonEmpty(
    prepareHead: (I) -> O,
    combine: (O, I) -> O,
): O = tail.fold(
    initial = head.let(prepareHead),
    operation = combine,
)

inline fun <I> NonEmptyList<I>.reduceNonEmpty(
    combine: (I, I) -> I,
): I = tail.fold(
    initial = head,
    operation = combine,
)

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