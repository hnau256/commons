package org.hnau.commons.app.projector.fractal.semantic.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.projector.fractal.FCheckBox
import org.hnau.commons.app.projector.fractal.FIcon
import org.hnau.commons.app.projector.fractal.FText
import org.hnau.commons.app.projector.fractal.FTextField
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.semantic.SItem
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.fractal.utils.Saturation
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.kotlin.foldNullable
import org.hnau.commons.kotlin.ifTrue

class InputProjector(
    private val title: String,
    private val icon: Drawable?,
    private val contentProjector: InputContentProjector,
    private val errorMessage: StateFlow<String?>,
) {

    private val itemDrawer: ItemDrawer = object : ItemDrawer {

        private val topAccessoryTitle: String? = contentProjector.fold(
            ifWithTitle = { null },
            ifWithoutTitle = { title },
        )


        @Composable
        override fun Item(
            onClick: (() -> Unit)?,
            endAccessory: @Composable (() -> Unit)?,
            content: @Composable (() -> Unit)
        ) {

            val errorMessage by errorMessage.collectAsState()
            UpdateFContext(
                update = {
                    errorMessage.foldNullable(
                        ifNull = { this },
                        ifNotNull = {
                            copy(
                                mood = Mood.Error,
                                saturation = Saturation.Active,
                            )
                        }
                    )
                }
            ) {
                SItem(
                    onClick = onClick,
                    startAccessory = icon?.let { iconNotNull ->
                        { FIcon(iconNotNull) }
                    },
                    endAccessory = endAccessory,
                    topAccessory = topAccessoryTitle?.let { title ->
                        { FText(title) }
                    },
                    bottomAccessory = errorMessage?.let { message ->
                        { FText(message) }
                    },
                    content = content,
                )
            }
        }
    }

    @Composable
    fun Content() {
        contentProjector
            .fold(
                ifWithTitle = { content ->
                    content(title, itemDrawer)
                },
                ifWithoutTitle = { content ->
                    content(itemDrawer)
                }
            )
    }
}

fun UiInputStateHolder<Boolean, InputType.Flag>.toFlagInputProjectorFactory(): InputProjectorPrototype<Boolean, InputType.Flag> =
    toInputProjectorPrototype { _, state, updateState ->
        InputContentProjector.WithTitle { title, itemDrawer ->
            val enabled by enabled.collectAsState()
            val isChecked by state.collectAsState()
            itemDrawer.Item(
                onClick = enabled.ifTrue { { updateState(!isChecked) } },
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


data class TextInputProjectorConfig(
    val keyboardType: KeyboardType,
    val capitalization: KeyboardCapitalization,
)

fun InputType.Edit.ContentType.toTextInputProjectorConfig(): TextInputProjectorConfig = when (this) {
    InputType.Edit.ContentType.Text -> TextInputProjectorConfig(
        keyboardType = KeyboardType.Text,
        capitalization = KeyboardCapitalization.Sentences,
    )

    InputType.Edit.ContentType.Integer -> TextInputProjectorConfig(
        keyboardType = KeyboardType.Number,
        capitalization = KeyboardCapitalization.None,
    )

    InputType.Edit.ContentType.Decimal -> TextInputProjectorConfig(
        keyboardType = KeyboardType.Decimal,
        capitalization = KeyboardCapitalization.None,
    )
}

fun UiInputStateHolder<String, InputType.Edit>.toTextInputProjectorFactory(
    imeAction: ImeAction = ImeAction.Default,
): InputProjectorPrototype<String, InputType.Edit> =
    toInputProjectorPrototype { type, state, updateState ->
        InputContentProjector.WithoutTitle { itemDrawer ->
            val enabled by enabled.collectAsState()
            val value by state.collectAsState()
            itemDrawer.Item(
                endAccessory = value
                    .isNotEmpty()
                    .and(enabled)
                    .ifTrue {
                        {
                            FIcon(
                                drawable = Drawable.Vector(Icons.Default.Cancel),
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { updateState("") }
                            )
                        }
                    }
            ) {
                val config = type.contentType.toTextInputProjectorConfig()
                FTextField(
                    value = value,
                    onValueChanged = updateState,
                    keyboardOptions = KeyboardOptions(
                        capitalization = config.capitalization,
                        imeAction = imeAction,
                        keyboardType = config.keyboardType,
                    ),
                    lineLimits = TextFieldLineLimits.SingleLine,
                    enabled = enabled,
                )
            }
        }
    }