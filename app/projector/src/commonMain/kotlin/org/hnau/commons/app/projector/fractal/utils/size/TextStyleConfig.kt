package org.hnau.commons.app.projector.fractal.utils.size

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import org.hnau.commons.app.projector.fractal.utils.BaseWithDecay
import org.hnau.commons.app.projector.fractal.utils.Distance
import org.hnau.commons.app.projector.fractal.utils.textUnit

data class TextStyleConfig(
    val size: TextUnit,
    val weight: FontWeight,
    val letterSpacing: TextUnit,
    val lineHeightFactor: Float,
) {

    private val letterSpacingWithDecay: BaseWithDecay<TextUnit> = BaseWithDecay.textUnit(
        initial = letterSpacing,
        decay = 1.2,
    )

    fun toTextStyle(
        distance: Distance,
    ): TextStyle {
        val fontSize = size.scale(distance.scale.content, 1.sp)
        return TextStyle(
            fontSize = fontSize,
            fontWeight = weight.coerceIn(FontWeight.Thin, FontWeight.Black),
            letterSpacing = letterSpacingWithDecay[distance],
            lineHeight = fontSize * lineHeightFactor,
        )
    }
}