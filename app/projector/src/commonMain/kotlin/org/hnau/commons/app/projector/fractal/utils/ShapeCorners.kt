package org.hnau.commons.app.projector.fractal.utils

import org.hnau.commons.app.projector.utils.Orientation

@JvmInline
value class ShapeCorners private constructor(
    private val packed: Byte,
) {

    fun interface Provider {

        fun getTableCorners(): ShapeCorners

        companion object {

            val opened = Provider { ShapeCorners.opened }
        }
    }

    constructor(
        startTopIsOpened: Boolean,
        startBottomIsOpened: Boolean,
        endTopIsOpened: Boolean,
        endBottomIsOpened: Boolean,
    ) : this(
        packed = ((if (startTopIsOpened) START_TOP else 0) +
                (if (startBottomIsOpened) START_BOTTOM else 0) +
                (if (endTopIsOpened) END_TOP else 0) +
                (if (endBottomIsOpened) END_BOTTOM else 0)).toByte()
    )

    val startTopIsOpened: Boolean
        get() = packed.toInt() and START_TOP != 0

    val startBottomIsOpened: Boolean
        get() = packed.toInt() and START_BOTTOM != 0

    val endTopIsOpened: Boolean
        get() = packed.toInt() and END_TOP != 0

    val endBottomIsOpened: Boolean
        get() = packed.toInt() and END_BOTTOM != 0

    private fun closePartially(
        closeStartTop: Boolean,
        closeStartBottom: Boolean,
        closeEndTop: Boolean,
        closeEndBottom: Boolean,
    ): ShapeCorners = ShapeCorners(
        startTopIsOpened = startTopIsOpened && !closeStartTop,
        startBottomIsOpened = startBottomIsOpened && !closeStartBottom,
        endTopIsOpened = endTopIsOpened && !closeEndTop,
        endBottomIsOpened = endBottomIsOpened && !closeEndBottom,
    )

    fun close(
        orientation: Orientation,
        startOrTop: Boolean,
        endOrBottom: Boolean,
    ): ShapeCorners = closePartially(
        closeStartTop = startOrTop,
        closeStartBottom = when (orientation) {
            Orientation.Vertical -> endOrBottom
            Orientation.Horizontal -> startOrTop
        },
        closeEndTop = when (orientation) {
            Orientation.Horizontal -> endOrBottom
            Orientation.Vertical -> startOrTop
        },
        closeEndBottom = endOrBottom,
    )

    override fun toString(): String = listOf(
        "startTop" to startTopIsOpened,
        "startBottom" to startBottomIsOpened,
        "endTop" to endTopIsOpened,
        "endBottom" to endBottomIsOpened,
    ).joinToString(
        prefix = "TableCorners(",
        postfix = ")",
    ) { (name, opened) ->
        "$name:${if (opened) "opened" else "closed"}"
    }

    companion object {

        private const val START_TOP = 0b0001
        private const val START_BOTTOM = 0b0010
        private const val END_TOP = 0b0100
        private const val END_BOTTOM = 0b1000

        val opened: ShapeCorners = ShapeCorners(
            startTopIsOpened = true,
            startBottomIsOpened = true,
            endTopIsOpened = true,
            endBottomIsOpened = true,
        )
    }
}