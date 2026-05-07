package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import org.hnau.commons.app.model.theme.color.Contrast
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.context.color
import org.hnau.commons.app.projector.fractal.context.newTone
import org.hnau.commons.app.projector.fractal.size.LocalSizeType
import org.hnau.commons.app.projector.fractal.size.SizeType
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.content

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
    autoSize: TextAutoSize? = null,
) {
    UpdateFContext(
        update = { newTone(Contrast.content) },
    ) {
        val fContext = LocalFContext.current
        BasicText(
            text = text,
            style = fContext.distance.units.textStyle[type].merge(
                color = fContext.color,
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