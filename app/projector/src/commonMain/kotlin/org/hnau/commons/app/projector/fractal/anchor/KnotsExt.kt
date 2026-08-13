package org.hnau.commons.app.projector.fractal.anchor

internal inline fun <T> List<T>.lerpAt(
    position: Float,
    crossinline lerp: (from: T, to: T, fraction: Float) -> T,
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

internal inline fun <T> List<T>.positionAt(
    value: Float,
    crossinline knot: (T) -> Float,
): Float {
    val v = value.takeIf(Float::isFinite) ?: 0f
    return when (val i = indexOfFirst { item -> v <= knot(item) }) {
        -1 -> lastIndex.toFloat()
        0 -> 0f
        else -> {
            val from = knot(this[i - 1])
            val to = knot(this[i])
            val fraction = if (to > from) (v - from) / (to - from) else 0f
            i - 1 + fraction
        }
    }
}
