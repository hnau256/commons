package org.hnau.commons.app.projector.utils

import androidx.compose.ui.unit.LayoutDirection

inline fun <R> LayoutDirection.fold(
    ifLtr: () -> R,
    ifRtl: () -> R,
): R = when (this) {
    LayoutDirection.Ltr -> ifLtr()
    LayoutDirection.Rtl -> ifRtl()
}