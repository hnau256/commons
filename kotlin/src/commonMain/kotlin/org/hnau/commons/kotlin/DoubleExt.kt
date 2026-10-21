package org.hnau.commons.kotlin


fun lerp(
    start: Double,
    stop: Double,
    fraction: Double,
): Double = start + ((stop - start) * fraction)