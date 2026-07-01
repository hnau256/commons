package org.hnau.commons.app.projector.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import org.hnau.commons.app.projector.uikit.utils.Dimens
import org.hnau.commons.kotlin.foldNullable


@Composable
fun Modifier.option(
    modifierOrNull: Modifier?,
): Modifier = modifierOrNull.foldNullable(
    ifNull = { this },
    ifNotNull = { then(it) }
)

@Composable
fun Modifier.option(
    buildModifierOrNull: @Composable Modifier.() -> Modifier?,
): Modifier = option(buildModifierOrNull())

fun Modifier.clickableOption(
    onClick: (() -> Unit)?,
    onClickLabel: String? = null,
    role: Role? = null,
): Modifier = when (onClick) {
    null -> this
    else -> clickable(
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick,
    )
}

val Dimens.horizontalDisplayPadding: Dp
    get() = separation

fun Modifier.horizontalDisplayPadding(): Modifier = padding(
    horizontal = Dimens.horizontalDisplayPadding,
)

val Dimens.verticalDisplayPadding: Dp
    get() = separation

fun Modifier.verticalDisplayPadding(): Modifier = padding(
    vertical = Dimens.verticalDisplayPadding,
)