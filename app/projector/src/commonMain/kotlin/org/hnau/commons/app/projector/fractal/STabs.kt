package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import arrow.core.NonEmptyList
import arrow.core.toNonEmptyListOrThrow
import org.hnau.commons.app.projector.fractal.anchor.SAnchors
import org.hnau.commons.app.projector.fractal.anchor.SAnchorsState
import org.hnau.commons.app.projector.fractal.anchor.rememberForIndex
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.padding.LocalContentPadding
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.Importance
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.rememberLet

@Composable
fun <T> STabs(
    items: NonEmptyList<T>,
    getSelection: () -> T,
    onSelectionChanged: ((T) -> Unit)?,
    modifier: Modifier = Modifier,
    importanceToActivate: Importance? = Importance.default,
    itemPaddingValues: PaddingValues = LocalDistance.current.units.paddingValues.horizontal.small,
    item: @Composable (item: T) -> Unit,
) {
    SAnchors(
        modifier = modifier,
        importanceToActivate = importanceToActivate,
        orientation = Orientation.Horizontal,
        weights = items.size.rememberLet { itemsCount ->
            (0 until itemsCount - 1).map { 1f }.toNonEmptyListOrThrow()
        },
        isEnabled = onSelectionChanged != null,
        state = SAnchorsState.rememberForIndex(
            getSelectedIndex = {
                getSelection()
                    .let(items::indexOf)
                    .takeIf { it >= 0 }
                    ?: 0
            },
            setSelectedIndex = onSelectionChanged?.let { updateSelection ->
                { index: Int -> updateSelection(items[index]) }
            }
        ),
        item = {
            CompositionLocalProvider(
                LocalContentPadding provides itemPaddingValues,
            ) {
                item(items[it])
            }
        },
    )
}