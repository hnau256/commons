package org.hnau.commons.app.projector.fractal.anchor

@JvmInline
value class Position(
    val position: Float,
) : Comparable<Position> {

    override fun compareTo(
        other: Position,
    ): Int = position.compareTo(
        other = other.position,
    )

    inline fun transform(
        block: (Float) -> Float,
    ): Position = block(position).let(::Position)

    inline fun combine(
        other: Position,
        block: (Float, Float) -> Float,
    ): Position = block(
        position,
        other.position,
    ).let(::Position)

    operator fun plus(
        other: Position,
    ): Position = combine(
        other = other,
        block = Float::plus,
    )

    operator fun minus(
        other: Position,
    ): Position = combine(
        other = other,
        block = Float::minus,
    )

    operator fun plus(
        other: Number,
    ): Position = transform { position ->
        position + other.toFloat()
    }

    operator fun minus(
        other: Number,
    ): Position = transform { position ->
        position - other.toFloat()
    }
}