package org.hnau.commons.app.projector.fractal.anchor

import arrow.core.NonEmptyList
import arrow.core.toNonEmptyListOrThrow
import org.hnau.commons.kotlin.foldBoolean

internal fun buildAnchors(weights: NonEmptyList<Float>): NonEmptyList<Anchor> =
    buildList {
        add(Anchor(weightBefore = 0f))
        val atLeastOneWeight = weights.any { weight -> weight > 0 }
        addAll(
            weights.map { weight ->
                Anchor(
                    weightBefore = atLeastOneWeight.foldBoolean(
                        ifTrue = { weight },
                        ifFalse = { 1f },
                    ),
                )
            },
        )
    }.toNonEmptyListOrThrow()

internal class SAnchorsMapper(anchors: NonEmptyList<Anchor>) {

    private val cumulativeWeights: List<Float> = anchors.runningFold(0f) { acc, anchor -> acc + anchor.weightBefore }
    private val totalWeight: Float = cumulativeWeights.last()
    private val anchorFractions: List<Float> = cumulativeWeights.drop(1).map { it / totalWeight }

    fun direct(position: Position): Along {
        val i = position.position.toInt().coerceIn(0, anchorFractions.lastIndex)
        val from = anchorFractions[i]
        val to = anchorFractions[(i + 1).coerceIn(0, anchorFractions.lastIndex)]
        return Along(from + (position.position - i) * (to - from))
    }

    fun reverse(along: Along): Position {
        var result: Position? = null
        var i = 0
        do {
            val from = anchorFractions[i]
            val to = anchorFractions[i + 1]
            result = when {
                along.along <= from -> i.toFloat().let(::Position)
                along.along <= to -> (i + (along.along - from) / (to - from)).let(::Position)
                else -> null
            }
            i++
        } while (result == null && i < anchorFractions.lastIndex)
        return result ?: anchorFractions.lastIndex.toFloat().let(::Position)
    }
}
