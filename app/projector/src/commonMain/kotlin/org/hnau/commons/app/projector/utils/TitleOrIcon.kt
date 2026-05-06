package org.hnau.commons.app.projector.utils

import androidx.compose.ui.graphics.vector.ImageVector
import org.hnau.commons.kotlin.it

sealed interface TitleOrIcon {

    data class Title(
        val title: String,
    ) : TitleOrIcon

    data class Icon(
        val icon: ImageVector,
    ) : TitleOrIcon

    data class Both(
        val title: String,
        val icon: ImageVector,
    ) : TitleOrIcon
}

inline fun <R> TitleOrIcon.fold(
    ifTitle: (title: String) -> R,
    ifIcon: (icon: ImageVector) -> R,
    ifBoth: (title: String, icon: ImageVector) -> R,
): R = when (this) {
    is TitleOrIcon.Title -> ifTitle(title)
    is TitleOrIcon.Icon -> ifIcon(icon)
    is TitleOrIcon.Both -> ifBoth(title, icon)
}

val TitleOrIcon.titleOrNull: String?
    get() = fold(
        ifTitle = ::it,
        ifIcon = { null },
        ifBoth = { title, _ -> title },
    )

val TitleOrIcon.iconOrNull: ImageVector?
    get() = fold(
        ifTitle = { null },
        ifIcon = ::it,
        ifBoth = { _, icon -> icon },
    )