package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import org.hnau.commons.app.model.theme.palette.PaletteType
import org.hnau.commons.app.projector.fractal.utils.SwitchPalette
import org.hnau.commons.app.projector.fractal.utils.color.localContent
import org.hnau.commons.app.projector.fractal.utils.local
import org.hnau.commons.app.projector.fractal.utils.size.FUnits
import org.hnau.commons.app.projector.fractal.utils.size.TextStyleType

@Composable
fun FText(
    text: String,
    modifier: Modifier = Modifier,
    type: TextStyleType = TextStyleType.Default,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    palette: PaletteType = PaletteType.local,
    autoSize: TextAutoSize? = null,
) {
    SwitchPalette(
        newPalette = palette,
    ) {
        BasicText(
            text = text,
            style = FUnits.local.textStyle[type].merge(
                color = Color.localContent,
            ),
            modifier = modifier,
            onTextLayout = onTextLayout,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            autoSize = autoSize,
        )
    }
}