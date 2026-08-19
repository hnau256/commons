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
        InputContentProjector.WithTitle { title, titleMaxLines, itemDrawer ->
            val updateOrNull by updateState.collectAsState()
            val selection = state.collectAsState()
            val variants = inputType.variants
            itemDrawer.Item(
                onClick = updateOrNull?.let{update ->
                    {
                        val selectedIndex = variants.indexOf(selection.value)
                        val newIndex = (selectedIndex + 1) % variants.size
                        update(variants[newIndex])
                    }
                },
                endAccessory = {
                    STabs(
                        items = inputType.variants,
                        getSelection = selection::value::get,
                        onSelectionChanged = updateOrNull,
                        item = item,
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