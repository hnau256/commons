package org.hnau.commons.app.projector.fractal.utils

@JvmInline
value class Importance(
    val importance: Int,
) {

    companion object {

        val high: Importance
            get() = Importance(0)

        val medium: Importance
            get() = Importance(1)

        val low: Importance
            get() = Importance(2)

        val default: Importance
            get() = high
    }
}