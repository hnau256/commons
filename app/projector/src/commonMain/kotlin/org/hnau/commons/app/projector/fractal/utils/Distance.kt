package org.hnau.commons.app.projector.fractal.utils

@JvmInline
value class Distance(
    val distance: Int,
) {

    companion object {

        val zero: Distance
            get() = Distance(0)
    }
}

operator fun Distance.plus(
    count: Int,
): Distance = Distance(
    distance = distance + count,
)

