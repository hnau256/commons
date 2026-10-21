package org.hnau.commons.app.projector.uikit.line.ext

import androidx.compose.ui.layout.IntrinsicMeasurable
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.fold

context(orientation: Orientation)
fun IntrinsicMeasurable.minIntrinsicAlong(
    across: Int,
): Int = orientation.fold(
    ifHorizontal = { minIntrinsicWidth(across) },
    ifVertical = { minIntrinsicHeight(across) },
)

context(orientation: Orientation)
fun IntrinsicMeasurable.maxIntrinsicAlong(
    across: Int,
): Int = orientation.fold(
    ifHorizontal = { maxIntrinsicWidth(across) },
    ifVertical = { maxIntrinsicHeight(across) },
)

context(orientation: Orientation)
fun IntrinsicMeasurable.minIntrinsicAcross(
    along: Int,
): Int = orientation.fold(
    ifHorizontal = { minIntrinsicHeight(along) },
    ifVertical = { minIntrinsicWidth(along) },
)

context(orientation: Orientation)
fun IntrinsicMeasurable.maxIntrinsicAcross(
    along: Int,
): Int = orientation.fold(
    ifHorizontal = { maxIntrinsicHeight(along) },
    ifVertical = { maxIntrinsicWidth(along) },
)