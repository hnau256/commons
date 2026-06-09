package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.hnau.commons.app.projector.fractal.distance.LocalDistance
import org.hnau.commons.app.projector.fractal.padding.LocalContentPadding
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.uikit.state.NullableStateContent
import org.hnau.commons.app.projector.uikit.state.StateContent
import org.hnau.commons.app.projector.uikit.transition.TransitionSpec
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.app.projector.utils.copy
import org.hnau.commons.app.projector.utils.iconOrNull
import org.hnau.commons.app.projector.utils.option
import org.hnau.commons.app.projector.utils.titleOrNull
import org.hnau.commons.kotlin.foldBoolean
import org.hnau.commons.kotlin.foldNullable
import org.hnau.commons.kotlin.it

@Composable
fun STitleOrIcon(
    titleOrIcon: TitleOrIcon,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.aligned(Alignment.CenterHorizontally),
    ) {
        val units = LocalDistance.current.units

        val contentPadding = LocalContentPadding.current

        val iconOrNull = titleOrIcon.iconOrNull
        val titleOrNull = titleOrIcon.titleOrNull

        Side(
            isEnd = false,
            spaceHeight = units.iconSize,
            value = iconOrNull,
            content = ::SIcon,
            contentPadding = contentPadding.copy(
                end = 0.dp,
            ),
        )

        val separation = units.padding.along.extraSmall
        separation
            .takeIf { iconOrNull != null && titleOrNull != null }
            .NullableStateContent(
                transitionSpec = TransitionSpec.rememberCenter(),
            ) { localSeparation ->
                Spacer(Modifier.width(localSeparation))
            }

        Side(
            isEnd = true,
            spaceHeight = null,
            value = titleOrNull,
            content = ::SText,
            contentPadding = contentPadding.copy(
                start = 0.dp,
            ),
        )
    }
}

@Composable
private fun <T : Any> Side(
    isEnd: Boolean,
    spaceHeight: Dp?,
    contentPadding: PaddingValues,
    value: T?,
    content: @Composable (T) -> Unit,
) {
    val alignment = isEnd.foldBoolean(
        ifTrue = { Alignment.CenterStart },
        ifFalse = { Alignment.CenterEnd },
    )
    value
        .StateContent(
            label = "STitleOrIconSide(isEnd=$isEnd)",
            contentKey = ::it,
            transitionSpec = TransitionSpec.remember(
                showAlignment = alignment,
                hideAlignment = alignment,
            ),
            contentAlignment = alignment,
        ) { localValueOrNull ->
            localValueOrNull.foldNullable(
                ifNull = {
                    Spacer(
                        Modifier
                            .padding(contentPadding)
                            .option(spaceHeight?.let(Modifier::height))
                    )
                },
                ifNotNull = { localValue ->
                    CompositionLocalProvider(
                        value = LocalContentPadding provides contentPadding,
                    ) {
                        content(localValue)
                    }
                }
            )
        }
}
