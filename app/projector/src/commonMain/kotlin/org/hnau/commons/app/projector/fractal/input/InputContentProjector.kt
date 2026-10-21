package org.hnau.commons.app.projector.fractal.input

import androidx.compose.runtime.Composable

sealed interface InputContentProjector {

    data class WithTitle(
        val content: @Composable (title: String, titleMaxLines: Int, item: ItemDrawer) -> Unit,
    ) : InputContentProjector

    data class WithoutTitle(
        val content: @Composable (item: ItemDrawer) -> Unit,
    ) : InputContentProjector

    companion object
}

inline fun <R> InputContentProjector.fold(
    ifWithTitle: (content: @Composable (title: String, titleMaxLines: Int, item: ItemDrawer) -> Unit) -> R,
    ifWithoutTitle: (content: @Composable (item: ItemDrawer) -> Unit) -> R,
): R = when (this) {
    is InputContentProjector.WithTitle -> ifWithTitle(content)
    is InputContentProjector.WithoutTitle -> ifWithoutTitle(content)
}