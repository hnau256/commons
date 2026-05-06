package org.hnau.commons.app.projector.fractal

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.VisualTransformation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.hnau.commons.app.model.EditingString
import org.hnau.commons.app.model.theme.palette.PaletteType
import org.hnau.commons.app.projector.fractal.utils.OffsetDistance
import org.hnau.commons.app.projector.fractal.utils.SwitchPalette
import org.hnau.commons.app.projector.fractal.utils.color.localContent
import org.hnau.commons.app.projector.fractal.utils.orInactive
import org.hnau.commons.app.projector.fractal.utils.size.FUnits
import org.hnau.commons.app.projector.fractal.utils.size.SpaceSize
import org.hnau.commons.app.projector.fractal.utils.size.TextStyleType
import org.hnau.commons.app.projector.fractal.utils.size.fPadding
import org.hnau.commons.app.projector.uikit.state.NullableStateContent
import org.hnau.commons.app.projector.uikit.transition.TransitionSpec
import org.hnau.commons.app.projector.utils.Side
import org.hnau.commons.app.projector.utils.fold
import org.hnau.commons.app.projector.utils.orientation
import org.hnau.commons.app.projector.utils.textFieldValueMapper

@Composable
fun FTextField(
    value: MutableStateFlow<EditingString>,
    modifier: Modifier = Modifier,
    palette: PaletteType = PaletteType.default,
    startAccessory: @Composable (() -> Unit)? = null,
    topAccessory: @Composable (() -> Unit)? = null,
    endAccessory: @Composable (() -> Unit)? = null,
    bottomAccessory: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyleType = TextStyleType.Body,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    maxLines: Int = 1,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource? = null,
) {
    var isFocused: Boolean by remember { mutableStateOf(false) }
    SwitchPalette(
        newPalette = palette.orInactive(
            active = isFocused,
        )
    ) {

        val units = FUnits.local
        val color = Color.localContent

        var localValue by remember { mutableStateOf(value.value.let(mapper.direct)) }
        LaunchedEffect(value) {
            value.collect { value ->
                localValue = value.let(mapper.direct)
            }
        }

        val bringIntoViewRequester = remember { BringIntoViewRequester() }
        val coroutineScope = rememberCoroutineScope()

        BasicTextField(
            value = localValue,
            onValueChange = { newValue ->
                value.value = newValue.let(mapper.reverse)
                localValue = newValue
            },
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
            readOnly = readOnly,
            textStyle = units.textStyle[textStyle].merge(
                color = Color.localContent,
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            maxLines = maxLines,
            minLines = minLines,
            singleLine = minLines <= 1,
            visualTransformation = visualTransformation,
            onTextLayout = onTextLayout,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(color),
            decorationBox = { textInput ->
                Row(
                    modifier = Modifier
                        .border(
                            width = units.borderWidth,
                            color = Color.localContent,
                            shape = units.borderShape,
                        )
                        .fPadding(
                            spaceSize = SpaceSize.Medium,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Accessory(
                        side = Side.Start,
                        accessory = startAccessory,
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        Accessory(
                            side = Side.Top,
                            accessory = topAccessory,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        textInput()
                        Accessory(
                            side = Side.Bottom,
                            accessory = bottomAccessory,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Accessory(
                        side = Side.End,
                        accessory = endAccessory,
                    )
                }
            }
        )
    }
}

@Composable
private fun Accessory(
    side: Side,
    modifier: Modifier = Modifier,
    accessory: @Composable (() -> Unit)?
) {
    val orientation = side.orientation
    val align = side.fold(
        ifStart = { Alignment.CenterEnd },
        ifTop = { Alignment.BottomStart },
        ifEnd = { Alignment.CenterStart },
        ifBottom = { Alignment.TopStart },
    )
    val space = FUnits.local.padding[orientation].small
    Box(
        modifier = modifier.then(
            side.fold(
                ifStart = { Modifier.padding(end = space) },
                ifTop = { Modifier.padding(bottom = space) },
                ifEnd = { Modifier.padding(start = space) },
                ifBottom = { Modifier.padding(top = space) }
            )
        ),
        contentAlignment = align,
    ) {
        accessory.NullableStateContent(
            contentAlignment = align,
            transitionSpec = TransitionSpec.remember(align, align),
        ) { localAccessory ->
            OffsetDistance(
                offset = 1,
            ) {
                localAccessory()
            }
        }
    }
}

private val mapper = EditingString.textFieldValueMapper
