package org.hnau.commons.app.projector.fractal.semantic.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.model.input.InputStateHolder
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
import org.hnau.commons.kotlin.KeyValue
import org.hnau.commons.kotlin.coroutines.flow.state.mapState
import org.hnau.commons.kotlin.foldNullable
import org.hnau.commons.kotlin.ifTrue

class SInputProjector(
    private val title: String,
    private val icon: Drawable?,
    private val contentProjector: SInputContentProjector,
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

    interface Factory {

        fun createSInputProjector(
            scope: CoroutineScope,
            title: String,
            icon: Drawable?,
        ): SInputProjector
    }
}

data class ErrorAsStringInputStateHolder<S, I : InputType<S>>(
    val type: I,
    val enabled: StateFlow<Boolean>,
    val createStateWithErrorOrNull: (CoroutineScope) -> StateFlow<KeyValue<S, String?>>,
    val updateState: (newState: S) -> Unit,
)

inline fun <S, E, I : InputType<S>> InputStateHolder<S, E, I>.toErrorAsStringInputStateHolder(
    crossinline parseError: (S, E) -> String,
): ErrorAsStringInputStateHolder<S, I> = ErrorAsStringInputStateHolder(
    type = type,
    updateState = ::updateState,
    enabled = enabled,
    createStateWithErrorOrNull = { scope ->
        stateWithErrorOrNone.mapState(scope) { stateWithErrorOrNone ->
            stateWithErrorOrNone.map { errorOrNone ->
                errorOrNone.map { error ->
                    val state = stateWithErrorOrNone.key
                    parseError(state, error)
                }.getOrNull()
            }
        }
    }
)

fun <S, I : InputType<S>> InputStateHolder<S, Nothing, I>.toErrorAsStringInputStateHolder(): ErrorAsStringInputStateHolder<S, I> =
    toErrorAsStringInputStateHolder { _, _ -> "" }

fun <S, I : InputType<S>> ErrorAsStringInputStateHolder<S, I>.toSInputProjectorFactory(
    createContentProjector: (type: I, state: StateFlow<S>, updateState: (S) -> Unit) -> SInputContentProjector,
): SInputProjector.Factory = object : SInputProjector.Factory {

    override fun createSInputProjector(
        scope: CoroutineScope,
        title: String,
        icon: Drawable?
    ): SInputProjector {
        val stateWithErrorOrNull = createStateWithErrorOrNull(scope)
        return SInputProjector(
            title = title,
            icon = icon,
            errorMessage = stateWithErrorOrNull.mapState(
                scope = scope,
                transform = KeyValue<*, String?>::value
            ),
            contentProjector = createContentProjector(
                type,
                stateWithErrorOrNull.mapState(
                    scope = scope,
                    transform = KeyValue<S, *>::key,
                ),
                updateState,
            )
        )
    }
}

fun ErrorAsStringInputStateHolder<Boolean, InputType.Flag>.toFlagSInputProjectorFactory(): SInputProjector.Factory =
    toSInputProjectorFactory { _, state, updateState ->
        SInputContentProjector.WithTitle { title, itemDrawer ->
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

fun ErrorAsStringInputStateHolder<String, InputType.Edit>.toTextSInputProjectorFactory(): SInputProjector.Factory =
    toSInputProjectorFactory { _, state, updateState ->
        SInputContentProjector.WithoutTitle { itemDrawer ->
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
                FTextField(
                    value = value,
                    onValueChanged = updateState,
                    /*keyboardOptions = KeyboardOptions(
                        capitalization = config.capitalization,
                        imeAction = imeAction,
                        keyboardType = config.keyboardType,
                    ),*/
                    lineLimits = TextFieldLineLimits.SingleLine,
                    enabled = enabled,
                )
            }
        }
    }