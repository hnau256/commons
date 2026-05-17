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
import org.hnau.commons.kotlin.ifTrue


@JvmName("toFlagInputProjectorPrototype")
fun InputStateHolder<Boolean, Nothing, InputType.Flag>.toInputProjectorPrototype(): InputProjectorPrototype<Boolean, Nothing, InputType.Flag> =
    toInputProjectorPrototype { _, state, updateState ->
        InputContentProjector.WithTitle { title, itemDrawer ->
            val enabled by enabled.collectAsState()
            val isChecked by state.collectAsState()
            itemDrawer.Item(
                onClick = enabled.ifTrue { { updateState(!isChecked) } },
                endAccessory = {
                    SCheckBox(
                        isChecked = isChecked
                    )
                }
            ) {
                SText(title)
            }
        }
    }