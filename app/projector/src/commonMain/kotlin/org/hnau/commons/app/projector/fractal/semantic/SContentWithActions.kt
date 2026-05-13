package org.hnau.commons.app.projector.fractal.semantic

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.hnau.commons.app.projector.fractal.FButton
import org.hnau.commons.app.projector.fractal.ForceFill
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.semantic.utils.LocalSContentPadding
import org.hnau.commons.app.projector.fractal.size.SizeType
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.utils.Orientation
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress

@Composable
fun SContentWithActions(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    actions: @Composable SActionsScope.() -> Unit,
) {
    SLine(
        modifier = modifier.fillMaxWidth(),
        orientation = Orientation.Vertical,
        forceFill = ForceFill.First,
        separation = SizeType.Large,
    ) {
        content()
        SActions(
            modifier = Modifier.fillMaxWidth(),
            block = actions,
        )
    }
}

