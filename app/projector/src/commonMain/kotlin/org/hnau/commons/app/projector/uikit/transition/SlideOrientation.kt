package org.hnau.commons.app.projector.uikit.transition

enum class SlideOrientation { Horizontal, Vertical }

inline fun <R> SlideOrientation.fold(
    ifHorizontal: () -> R,
    ifVertical: () -> R,
): R = when (this) {
    SlideOrientation.Horizontal -> ifHorizontal()
    SlideOrientation.Vertical -> ifVertical()
}