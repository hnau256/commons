package org.hnau.commons.app.projector.fractal.semantic.input.type

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.projector.fractal.FIcon
import org.hnau.commons.app.projector.fractal.FTextField
import org.hnau.commons.app.projector.fractal.semantic.input.InputContentProjector
import org.hnau.commons.app.projector.fractal.semantic.input.InputProjectorPrototype
import org.hnau.commons.app.projector.fractal.semantic.input.UiInputStateHolder
import org.hnau.commons.app.projector.fractal.semantic.input.toInputProjectorPrototype
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.kotlin.ifTrue

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

@JvmName("toEditInputProjectorPrototype")
fun UiInputStateHolder<String, InputType.Edit>.toInputProjectorPrototype(
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