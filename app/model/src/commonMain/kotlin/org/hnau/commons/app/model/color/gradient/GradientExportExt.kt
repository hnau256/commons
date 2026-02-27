package org.hnau.commons.app.model.color.gradient

import org.hnau.commons.kotlin.sumOf

fun <C> Gradient<C>.export(): Array<Pair<Float, C>> {
    val totalWeight = tail.sumOf(Gradient.Step<C>::weight)
    var collectedWeight = 0f
    return Array(tail.size + 1) { i ->
        when (i) {
            0 -> Pair(0f, head)
            else -> {
                val (weight, color) = tail[i - 1]
                collectedWeight += weight
                val offset = collectedWeight / totalWeight
                offset to color
            }
        }
    }
}
