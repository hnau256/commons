package org.hnau.commons.app.projector.fractal.input.type

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.hnau.commons.app.model.input.InputStateHolder
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.projector.fractal.STabs
import org.hnau.commons.app.projector.fractal.SText
import org.hnau.commons.app.projector.fractal.input.InputContentProjector
import org.hnau.commons.app.projector.fractal.input.InputProjectorPrototype
import org.hnau.commons.app.projector.fractal.input.toInputProjectorPrototype


@JvmName("toVariantInputProjectorPrototype")
fun <S> InputStateHolder<S, Nothing, InputType.Variant<S>>.toInputProjectorPrototype(
    item: @Composable (S) -> Unit,
): InputProjectorPrototype<S, Nothing, InputType.Variant<S>> =
    toInputProjectorPrototype { inputType, state, updateState ->
        InputContentProjector.WithTitle { title, itemDrawer ->
            val enabled by enabled.collectAsState()
            val selection by state.collectAsState()
            with(itemDrawer) {
                val variants = inputType.variants
                Item(
                    onClick = {
                        val selectedIndex = variants.indexOf(selection)
                        val newIndex = (selectedIndex + 1) % variants.size
                        updateState(variants[newIndex])
                    },
                    endAccessory = {
                        STabs(
                            items = inputType.variants,
                            selection = selection,
                            onClick = updateState.takeIf { enabled },
                            item = item,
                        )
                    }
                ) {
                    SText(title)
                }
            }
        }
    }