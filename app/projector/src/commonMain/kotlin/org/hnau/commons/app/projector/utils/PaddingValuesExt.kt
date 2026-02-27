package org.hnau.commons.app.projector.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.takeOrElse

@Composable
inline fun PaddingValues.map(
    start: (Dp) -> Dp = { Dp.Unspecified },
    top: (Dp) -> Dp = { Dp.Unspecified },
    end: (Dp) -> Dp = { Dp.Unspecified },
    bottom: (Dp) -> Dp = { Dp.Unspecified },
): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        start = calculateStartPadding(layoutDirection).let { startValue ->
            start(startValue).takeOrElse { startValue }
        },
        top = calculateTopPadding().let { topValue ->
            top(topValue).takeOrElse { topValue }
        },
        end = calculateEndPadding(layoutDirection).let { endValue ->
            end(endValue).takeOrElse { endValue }
        },
        bottom = calculateBottomPadding().let { bottomValue ->
            bottom(bottomValue).takeOrElse { bottomValue }
        },
    )
}

@Composable
inline fun PaddingValues.map(
    transform: (Dp) -> Dp,
): PaddingValues = map(
    start = transform,
    top = transform,
    end = transform,
    bottom = transform,
)

@Composable
fun PaddingValues.copy(
    start: Dp = Dp.Unspecified,
    top: Dp = Dp.Unspecified,
    end: Dp = Dp.Unspecified,
    bottom: Dp = Dp.Unspecified,
): PaddingValues = map(
    start = { current -> start.takeOrElse { current } },
    top = { current -> top.takeOrElse { current } },
    end = { current -> end.takeOrElse { current } },
    bottom = { current -> bottom.takeOrElse { current } },
)

@Composable
fun PaddingValues.combineWith(
    other: PaddingValues,
    start: (Dp, other: Dp) -> Dp,
    top: (Dp, other: Dp) -> Dp,
    end: (Dp, other: Dp) -> Dp,
    bottom: (Dp, other: Dp) -> Dp,
): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        start = start(
            calculateStartPadding(layoutDirection),
            other.calculateStartPadding(layoutDirection)
        ),
        top = top(
            calculateTopPadding(),
            other.calculateTopPadding()
        ),
        end = end(
            calculateEndPadding(layoutDirection),
            other.calculateEndPadding(layoutDirection)
        ),
        bottom = bottom(
            calculateBottomPadding(),
            other.calculateBottomPadding()
        ),
    )
}

@Composable
fun PaddingValues.combineWith(
    other: PaddingValues,
    combine: (Dp, other: Dp) -> Dp,
): PaddingValues = combineWith(
    other = other,
    start = combine,
    top = combine,
    end = combine,
    bottom = combine,
)

@Composable
operator fun PaddingValues.plus(
    other: PaddingValues,
): PaddingValues = combineWith(
    other = other,
    combine = Dp::plus,
)

val PaddingValuesZero: PaddingValues = PaddingValues(
    all = 0.dp,
)