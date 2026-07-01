package org.hnau.commons.app.model.color.gradient

import org.hnau.commons.kotlin.sumOf

fun <C> Gradient<C>.valueAt(
    fraction: Float,
    lerp: (C, C, Float) -> C,
): C {

    val totalWeight = tail
        .sumOf(Gradient.Step<C>::weight)
        .takeIf { it > 0 }
        ?: return head

    val clamped = fraction.coerceIn(0f, 1f)

    var cumulativeWeight = 0f
    var prev = head
    var prevFraction = 0f
    tail.forEach { (weight, color) ->
        cumulativeWeight += weight
        val nextFraction = cumulativeWeight / totalWeight
        if (clamped <= nextFraction) {
            return lerp(
                prev,
                color,
                (nextFraction - prevFraction)
                    .takeIf { it > 0 }
                    ?.let { delta -> (clamped - prevFraction) / delta }
                    ?: 0f,
            )
        }
        prev = color
        prevFraction = nextFraction
    }
    return head
}
