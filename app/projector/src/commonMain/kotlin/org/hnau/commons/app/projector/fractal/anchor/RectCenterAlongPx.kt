package org.hnau.commons.app.projector.fractal.anchor

@JvmInline
value class RectCenterAlongPx(
    val along: Float,
) : Comparable<RectCenterAlongPx> {

    override fun compareTo(
        other: RectCenterAlongPx,
    ): Int = along.compareTo(
        other = other.along,
    )

    inline fun combine(
        other: RectCenterAlongPx,
        block: (Float, Float) -> Float,
    ): RectCenterAlongPx = block(
        along,
        other.along,
    ).let(::RectCenterAlongPx)

    operator fun plus(
        other: RectCenterAlongPx,
    ): RectCenterAlongPx = combine(
        other = other,
        block = Float::plus,
    )

    operator fun minus(
        other: RectCenterAlongPx,
    ): RectCenterAlongPx = combine(
        other = other,
        block = Float::minus,
    )
}