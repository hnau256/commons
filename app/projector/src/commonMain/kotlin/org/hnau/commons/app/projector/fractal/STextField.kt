package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.context.contentColor
import org.hnau.commons.app.projector.fractal.size.SizeType
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.Saturation
import org.hnau.commons.app.projector.utils.Orientation

@Composable
fun STextField(
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: SizeType = SizeType.default,
    enabled: Boolean = true,
    inputTransformation: InputTransformation? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    outputTransformation: OutputTransformation? = null,
    scrollState: ScrollState = rememberScrollState(),
) {
    var isFocused: Boolean by remember { mutableStateOf(false) }
    UpdateFContext(
        update = {
            copy(
                saturation = Saturation.get(isFocused)
            )
        }
    ) {

        val internalState = rememberSaveable(saver = TextFieldState.Saver) {
            TextFieldState(value)
        }

        LaunchedEffect(internalState) {
            snapshotFlow { internalState.text }.collect { newUiText ->
                onValueChanged(newUiText.toString())
            }
        }

        LaunchedEffect(value) {
            if (value != internalState.text.toString()) {
                internalState.setTextAndPlaceCursorAtEnd(value)
            }
        }

        val fContext = LocalFContext.current
        val units = fContext.distance.units
        val color = fContext.contentColor

        val bringIntoViewRequester = remember { BringIntoViewRequester() }
        val coroutineScope = rememberCoroutineScope()

        BasicTextField(
            state = internalState,
            modifier = modifier
                .bringIntoViewRequester(bringIntoViewRequester)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                    if (isFocused) {
                        coroutineScope.launch {
                            delay(100)
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                },
            enabled = enabled,
            readOnly = !enabled,
            textStyle = units.textStyle[textStyle].merge(
                color = color,
            ),
            keyboardOptions = keyboardOptions,
            lineLimits = lineLimits,
            inputTransformation = inputTransformation,
            outputTransformation = outputTransformation,
            onKeyboardAction = onKeyboardAction,
            onTextLayout = onTextLayout,
            interactionSource = interactionSource,
            scrollState = scrollState,
            cursorBrush = SolidColor(color),
            decorator = remember(
                color,
            ) {
                Decorator(
                    color = color,
                )
            },
        )
    }
}

private data class Decorator(
    private val color: Color,
) : TextFieldDecorator {

    @Composable
    override fun Decoration(
        innerTextField: @Composable (() -> Unit),
    ) {
        val units = LocalFContext.current.distance.units
        SLine(
            modifier = Modifier,
            orientation = Orientation.Vertical,
            separation = SizeType.ExtraSmall,
        ) {
            innerTextField()
            Spacer(
                modifier = Modifier
                    .height(units.borderWidth)
                    .background(color),
            )
        }
    }
}
