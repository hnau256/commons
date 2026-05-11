package org.hnau.commons.app.projector.fractal.semantic.input

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.app.model.input.InputModel
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.projector.fractal.FCheckBox
import org.hnau.commons.app.projector.fractal.FText
import org.hnau.commons.app.projector.utils.collectAsMutableAccessor
import org.hnau.commons.kotlin.ifTrue

fun InputModel<Boolean, Nothing, Boolean, InputType.Flag>.toContentProjector(
    scope: CoroutineScope,
): SInputProjectorFactory<Boolean, Nothing, Boolean, InputType.Flag> = toSInputProjectorFactory(
    scope = scope,
) {
    SInputContentProjector.WithTitle { title, itemDrawer ->
        val enabled by enabled.collectAsState()
        var isChecked by state.collectAsMutableAccessor()
        itemDrawer.Item(
            onClick = enabled.ifTrue { { isChecked = !isChecked } },
            endAccessory = {
                FCheckBox(
                    isChecked = isChecked
                )
            }
        ) {
            FText(title)
        }
    }
}