package org.hnau.commons.app.projector.uikit.line

@JvmInline
value class LinePosition private constructor(
    private val packed: Byte,
) {

    constructor(
        isFirst: Boolean,
        isLast: Boolean,
    ) : this(
        packed = ((if (isFirst) FIRST else 0) +
                (if (isLast) LAST else 0)).toByte()
    )

    val isFirst: Boolean
        get() = packed.toInt() and FIRST != 0

    val isLast: Boolean
        get() = packed.toInt() and LAST != 0

    override fun toString(): String =
        "LinePosition(isFirst=$isFirst,isLast=$isLast)"

    companion object {

        private const val FIRST = 0b01
        private const val LAST = 0b10
    }
}
