package org.hnau.commons.app.projector.fractal.input

import androidx.compose.runtime.Composable
import org.hnau.commons.app.projector.fractal.table.SCellScope

sealed interface InputContentProjector {

    data class WithTitle(
        val content: @Composable SCellScope.(title: String, item: ItemDrawer) -> Unit,
    ) : InputContentProjector

    data class WithoutTitle(
        val content: @Composable SCellScope.(item: ItemDrawer) -> Unit,
    ) : InputContentProjector

    companion object
}

inline fun <R> InputContentProjector.fold(
    ifWithTitle: (content: @Composable SCellScope.(title: String, item: ItemDrawer) -> Unit) -> R,
    ifWithoutTitle: (content: @Composable SCellScope.(item: ItemDrawer) -> Unit) -> R,
): R = when (this) {
    is InputContentProjector.WithTitle -> ifWithTitle(content)
    is InputContentProjector.WithoutTitle -> ifWithoutTitle(content)
}