package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import org.hnau.commons.app.model.theme.palette.PaletteType
import org.hnau.commons.app.projector.fractal.utils.LocalDistance
import org.hnau.commons.app.projector.fractal.utils.LocalPalette
import org.hnau.commons.app.projector.fractal.utils.SwitchPalette
import org.hnau.commons.app.projector.fractal.utils.color.localContent
import org.hnau.commons.app.projector.fractal.utils.size.LocalSizeType
import org.hnau.commons.app.projector.fractal.utils.size.SizeType
import org.hnau.commons.app.projector.fractal.utils.size.units

@Composable
fun FText(
    text: String,
    modifier: Modifier = Modifier,
    type: SizeType = LocalSizeType.current,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    palette: PaletteType = LocalPalette.current,
    autoSize: TextAutoSize? = null,
) {
    SwitchPalette(
        newPalette = palette,
    ) {
        BasicText(
            text = text,
            style = LocalDistance.current.units.textStyle[type].merge(
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