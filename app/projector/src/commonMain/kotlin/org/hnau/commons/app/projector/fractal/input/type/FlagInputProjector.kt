package org.hnau.commons.app.projector.fractal.input.type

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.hnau.commons.app.model.input.InputStateHolder
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.projector.fractal.SCheckBox
import org.hnau.commons.app.projector.fractal.SText
import org.hnau.commons.app.projector.fractal.input.InputContentProjector
import org.hnau.commons.app.projector.fractal.input.InputProjectorPrototype
import org.hnau.commons.app.projector.fractal.input.toInputProjectorPrototype


@JvmName("toFlagInputProjectorPrototype")
fun InputStateHolder<Boolean, Nothing, InputType.Flag>.toInputProjectorPrototype(): InputProjectorPrototype<Boolean, Nothing, InputType.Flag> =
    toInputProjectorPrototype { _, state, updateState ->
        InputContentProjector.WithTitle { title, titleMaxLines, itemDrawer ->
            val updateOrNull by updateState.collectAsState()
            val isChecked by state.collectAsState()
            itemDrawer.Item(
                onClick = updateOrNull?.let { update -> { update(!isChecked) } },
                endAccessory = {
                    SCheckBox(
                        isChecked = isChecked
                    )
                }
            ) {
                SText(
                    text = title,
                    maxLines = titleMaxLines,
                )
            }
        }
    }