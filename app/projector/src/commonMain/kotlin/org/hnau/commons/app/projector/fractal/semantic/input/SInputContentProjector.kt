package org.hnau.commons.app.projector.fractal.semantic.input

import androidx.compose.runtime.Composable

sealed interface SInputContentProjector {

    data class WithTitle(
        val content: @Composable (title: String, item: ItemDrawer) -> Unit,
    ) : SInputContentProjector

    data class WithoutTitle(
        val content: @Composable (item: ItemDrawer) -> Unit,
    ) : SInputContentProjector

    companion object
}

inline fun <R> SInputContentProjector.fold(
    ifWithTitle: (content: @Composable (title: String, item: ItemDrawer) -> Unit) -> R,
    ifWithoutTitle: (content: @Composable (item: ItemDrawer) -> Unit) -> R,
): R = when (this) {
    is SInputContentProjector.WithTitle -> ifWithTitle(content)
    is SInputContentProjector.WithoutTitle -> ifWithoutTitle(content)
}