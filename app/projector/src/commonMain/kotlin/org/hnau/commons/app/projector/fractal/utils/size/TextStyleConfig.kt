package org.hnau.commons.app.projector.fractal.utils.size

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import org.hnau.commons.app.projector.fractal.utils.BaseWithDecay

data class TextStyleConfig(
    val size: TextUnit,
    val weight: BaseWithDecay<FontWeight>,
    val letterSpacing: BaseWithDecay<TextUnit>,
) {

    fun toTextStyle(
        distance: org.hnau.commons.app.projector.fractal.utils.Distance,
    ): TextStyle = TextStyle(
        fontSize = 20.sp.scale(distance.scale.content, 1.sp),
        fontWeight = weight[distance].coerceIn(FontWeight.Thin, FontWeight.Black),
        letterSpacing = letterSpacing[distance],
    )
}