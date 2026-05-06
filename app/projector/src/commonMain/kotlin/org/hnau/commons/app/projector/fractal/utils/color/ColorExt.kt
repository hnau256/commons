package org.hnau.commons.app.projector.fractal.utils.color

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.hnau.commons.app.projector.utils.theme.getLocalColor

val Color.Companion.localBackground: Color
    @Composable
    get() = getLocalColor(
        type = ColorType.Background,
    )

val Color.Companion.localContent: Color
    @Composable
    get() = getLocalColor(
        type = ColorType.Content,
    )