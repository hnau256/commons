package org.hnau.commons.kotlin

fun <T: Comparable<T>> requireInRange(
    valueLabel: String,
    value: T,
    range: ClosedRange<T>,
) {
    require(
        value in range,
    ) {
        "$valueLabel must be in $range, actual: $value"
    }
}