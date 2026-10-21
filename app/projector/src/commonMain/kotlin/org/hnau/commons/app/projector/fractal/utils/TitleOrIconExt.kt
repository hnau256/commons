package org.hnau.commons.app.projector.fractal.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress
import org.hnau.commons.kotlin.foldNullable

fun TitleOrIcon.withActionOrElse(
    actionOrElseOrDisabled: ActionOrElse<Unit, *>?,
): TitleOrIcon {
    val iconToOverwrite = when (actionOrElseOrDisabled) {
        is ActionOrElse.Action<*>, null -> null
        is ActionOrElse.Else<*> -> when (actionOrElseOrDisabled.cancelOrInProgress) {
            is CancelOrInProgress.Cancel -> Drawable.Vector(Icons.Default.Clear)
            CancelOrInProgress.InProgress -> null
        }
    }

    return fold(
        ifTitle = { title ->
            iconToOverwrite.foldNullable(
                ifNull = { TitleOrIcon.Title(title) },
                ifNotNull = { icon -> TitleOrIcon.Both(title, icon) },
            )
        },
        ifIcon = { icon ->
            TitleOrIcon.Icon(iconToOverwrite ?: icon)
        },
        ifBoth = { title, icon ->
            TitleOrIcon.Both(
                title = title,
                icon = iconToOverwrite ?: icon,
            )
        }
    )
}