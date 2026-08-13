package org.hnau.commons.app.projector.fractal.anchor

import arrow.core.NonEmptyList

internal fun buildAnchors(
    weights: NonEmptyList<Float>,
): NonEmptyList<Anchor> {
    val normalizedWeights = when (weights.any { weight -> weight > 0f }) {
        true -> weights
        false -> weights.map { 1f }
    }
    return NonEmptyList(
        head = Anchor(weightBefore = 0f),
        tail = normalizedWeights.map { weight -> Anchor(weightBefore = weight) },
    )
}

internal class SAnchorsMapper(anchors: NonEmptyList<Anchor>) {

    private val cumulativeWeights: List<Float> = anchors.runningFold(0f) { acc, anchor -> acc + anchor.weightBefore }
    private val totalWeight: Float = cumulativeWeights.last()
    private val anchorFractions: List<Float> = cumulativeWeights.drop(1).map { it / totalWeight }

    fun direct(position: Position): Along {
        val value = position
            .position
            .takeIf(Float::isFinite)
            ?.coerceIn(0f, anchorFractions.lastIndex.toFloat())
            ?: 0f
        val i = value.toInt()
        val from = anchorFractions[i]
        val to = anchorFractions[(i + 1).coerceAtMost(anchorFractions.lastIndex)]
        return Along(from + (value - i) * (to - from))
    }

    fun reverse(along: Along): Position {

        val value = along.along
            .takeIf(Float::isFinite)
            ?.coerceIn(0f, 1f)
            ?: 0f

        val i = anchorFractions.indexOfFirst { fraction -> value <= fraction }

        return when (i) {
            -1 -> Position(anchorFractions.lastIndex.toFloat())
            0 -> Position(0f)
            else -> {
                val from = anchorFractions[i - 1]
                val to = anchorFractions[i]
                Position(i - 1 + (value - from) / (to - from))
            }
        }
    }
}
