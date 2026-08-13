package org.hnau.commons.app.projector.fractal.anchor

import androidx.compose.ui.util.lerp
import org.hnau.commons.kotlin.mapper.Mapper

internal fun <T> List<T>.knotsMapper(
    lerp: (from: T, to: T, fraction: Float) -> T,
    knot: (T) -> Float,
): Mapper<Float, T> = Mapper(
    direct = { position ->
        val value = position
            .takeIf(Float::isFinite)
            ?.coerceIn(0f, lastIndex.toFloat())
            ?: 0f
        val i = value.toInt()
        lerp(
            this[i],
            this[(i + 1).coerceAtMost(lastIndex)],
            value - i,
        )
    },
    reverse = { value ->
        val v = value.let(knot).takeIf(Float::isFinite) ?: 0f
        val i = indexOfFirst { item -> v <= knot(item) }
        when (i) {
            -1 -> lastIndex.toFloat()
            0 -> 0f
            else -> {
                val from = knot(this[i - 1])
                val to = knot(this[i])
                val fraction = if (to > from) (v - from) / (to - from) else 0f
                i - 1 + fraction
            }
        }},
)

internal fun List<Float>.knotsMapper(): Mapper<Float, Float> = knotsMapper(
    lerp = ::lerp,
    knot = { it },
)

private fun <T> List<T>.lerpAt(
    position: Float,
    lerp: (from: T, to: T, fraction: Float) -> T,
): T {
    val value = position
        .takeIf(Float::isFinite)
        ?.coerceIn(0f, lastIndex.toFloat())
        ?: 0f
    val i = value.toInt()
    return lerp(
        this[i],
        this[(i + 1).coerceAtMost(lastIndex)],
        value - i,
    )
}
