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


