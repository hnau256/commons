package org.hnau.commons.kotlin

inline fun <I : Comparable<I>, O : Comparable<O>> ClosedRange<I>.map(
    transform: (I) -> O,
): ClosedRange<O> = start.let(transform)..endInclusive.let(transform)