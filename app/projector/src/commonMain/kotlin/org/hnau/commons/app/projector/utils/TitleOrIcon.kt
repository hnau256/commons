package org.hnau.commons.app.projector.utils

import org.hnau.commons.gen.fold.annotations.Fold
import org.hnau.commons.kotlin.it

@Fold
sealed interface TitleOrIcon {

    data class Title(
        val title: String,
    ) : TitleOrIcon

    data class Icon(
        val icon: Drawable,
    ) : TitleOrIcon

    data class Both(
        val title: String,
        val icon: Drawable,
    ) : TitleOrIcon
}

val TitleOrIcon.titleOrNull: String?
    get() = fold(
        ifTitle = ::it,
        ifIcon = { null },
        ifBoth = { title, _ -> title },
    )

val TitleOrIcon.iconOrNull: Drawable?
    get() = fold(
        ifTitle = { null },
        ifIcon = ::it,
        ifBoth = { _, icon -> icon },
    )

val TitleOrIcon.asIcon: TitleOrIcon.Icon
    get() = fold(
        ifTitle = { title ->
            TitleOrIcon.Icon(
                icon = Drawable.Text(title)
            )
        },
        ifIcon = TitleOrIcon::Icon,
        ifBoth = { _, icon -> TitleOrIcon.Icon(icon) },
    )