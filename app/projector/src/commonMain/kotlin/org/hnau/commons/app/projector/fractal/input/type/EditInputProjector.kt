package org.hnau.commons.app.projector.fractal.input.type

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import org.hnau.commons.app.model.input.InputStateHolder
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.projector.fractal.SIcon
import org.hnau.commons.app.projector.fractal.STextField
import org.hnau.commons.app.projector.fractal.input.InputContentProjector
import org.hnau.commons.app.projector.fractal.input.InputProjectorPrototype
import org.hnau.commons.app.projector.fractal.input.toInputProjectorPrototype
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.kotlin.ifTrue


@JvmName("toEditInputProjectorPrototype")
fun <E> InputStateHolder<String, E, InputType.Edit>.toInputProjectorPrototype(
    imeAction: ImeAction = ImeAction.Default,
    keyboardType: KeyboardType,
    requestFocusOnStart: Boolean = false,
): InputProjectorPrototype<String, E, InputType.Edit> =
    toInputProjectorPrototype { _, state, updateState ->
        InputContentProjector.WithoutTitle { itemDrawer ->
            val enabled by enabled.collectAsState()
            val value by state.collectAsState()
            var isFocused: Boolean by remember { mutableStateOf(false) }
            val focusRequester = remember { FocusRequester() }
            if (requestFocusOnStart) {
                LaunchedEffect(focusRequester) { focusRequester.requestFocus() }
            }
            itemDrawer.Item(
                onClick = { focusRequester.requestFocus() },
                isFocused = isFocused,
                endAccessory = value
                    .isNotEmpty()
                    .and(enabled)
                    .ifTrue {
                        {
                            SIcon(
                                drawable = Drawable.Vector(Icons.Default.Close),
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { updateState("") }
                            )
                        }
                    }
            ) {
                STextField(
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused
                        },
                    value = value,
                    onValueChanged = updateState,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = imeAction,
                        keyboardType = keyboardType,
                    ),
                    lineLimits = TextFieldLineLimits.SingleLine,
                    enabled = enabled,
                )
            }
        }
    }