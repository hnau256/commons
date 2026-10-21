package org.hnau.commons.app.projector.fractal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import org.hnau.commons.app.projector.fractal.anchor.SAnchors
import org.hnau.commons.app.projector.fractal.anchor.SAnchorsState
import org.hnau.commons.app.projector.fractal.anchor.rememberForFraction
import org.hnau.commons.app.projector.fractal.utils.Importance
import org.hnau.commons.app.projector.utils.Orientation

@Composable
fun SProgress(
    getFraction: () -> Float,
    onFractionChanged: ((Float) -> Unit)?,
    modifier: Modifier = Modifier,
    importanceToActivate: Importance? = Importance.default,
) {
    SAnchors(
        modifier = modifier,
        importanceToActivate = importanceToActivate,
        orientation = Orientation.Horizontal,
        weights = singleItemWeights,
        isEnabled = onFractionChanged != null,
        state = SAnchorsState.rememberForFraction(
            getFraction = getFraction,
            setFraction = onFractionChanged,
        ),
        snap = false,
        drawProgress = true,
        item = null,
    )
}

private val singleItemWeights: NonEmptyList<Float> =
    nonEmptyListOf(1f)