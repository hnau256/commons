package org.hnau.commons.app.projector.fractal.anchor

import androidx.compose.ui.util.lerp
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

    fun direct(position: Position): Along =
        anchorFractions
            .lerpAt(position.position, ::lerp)
            .let(::Along)

    fun reverse(along: Along): Position =
        anchorFractions
            .positionAt(along.along) { it }
            .let(::Position)
}
