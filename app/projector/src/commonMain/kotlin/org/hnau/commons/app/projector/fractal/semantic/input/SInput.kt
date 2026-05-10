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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.flow.MutableStateFlow
import org.hnau.commons.app.projector.fractal.FCheckBox
import org.hnau.commons.app.projector.fractal.FIcon
import org.hnau.commons.app.projector.fractal.FItem
import org.hnau.commons.app.projector.fractal.FText
import org.hnau.commons.app.projector.fractal.FTextField
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.fractal.utils.Saturation
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.app.projector.utils.collectAsMutableAccessor
import org.hnau.commons.app.projector.utils.rememberRun
import org.hnau.commons.kotlin.coroutines.flow.state.mapState
import org.hnau.commons.kotlin.foldNullable
import org.hnau.commons.kotlin.ifTrue

@Composable
fun <S, E, V> SInput(
    title: String,
    icon: Drawable?,
    inputState: MutableStateFlow<SInputState<S, E, V>>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {

    val scope = rememberCoroutineScope()

    val inputContentDrawer by remember(scope, inputState, enabled) {
        inputState
            .toMutableState(scope)
            .mapState(scope) { (type, state) ->
                when (type) {
                    is SInputType.Edit -> SInputContentDrawer.edit(
                        type = type,
                        state = state as MutableStateFlow<String>,
                        enabled = enabled,
                    )

                    is SInputType.Flag -> SInputContentDrawer.flag(
                        type = type,
                        state = state as MutableStateFlow<Boolean>,
                        enabled = enabled,
                    )
                }
            }
    }
        .collectAsState()

    val errorMessage by remember(inputState, scope) {
        inputState.mapState(scope) { state ->
            state
                .valueOrError
                .mapLeft { error -> state.type.mapper.convertErrorToString(error) }
                .leftOrNull()
        }
    }.collectAsState()

    val itemDrawerFactory = rememberItemDrawerFactory(
        icon = icon,
        modifier = modifier,
        errorMessage = errorMessage,
    )

    when (val drawer = inputContentDrawer) {
        is SInputContentDrawer.WithTitle -> drawer.content(
            title,
            itemDrawerFactory.rememberItemDrawer(null),
        )

        is SInputContentDrawer.WithoutTitle -> drawer.content(
            itemDrawerFactory.rememberItemDrawer(title),
        )
    }
}

@Composable
private fun ItemDrawer.Factory.rememberItemDrawer(
    title: String?,
): ItemDrawer = rememberRun(title) {
    createItemDrawer(title)
}

@Composable
private fun rememberItemDrawerFactory(
    icon: Drawable?,
    errorMessage: String?,
    modifier: Modifier,
): ItemDrawer.Factory = remember(icon, errorMessage, modifier) {
    ItemDrawer.Factory { title ->
        object : ItemDrawer {

            @Composable
            override fun Item(
                onClick: (() -> Unit)?,
                endAccessory: @Composable (() -> Unit)?,
                content: @Composable (() -> Unit)
            ) {
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
                    FItem(
                        onClick = onClick,
                        modifier = modifier,
                        startAccessory = icon?.let { iconNotNull ->
                            { FIcon(iconNotNull) }
                        },
                        endAccessory = endAccessory,
                        topAccessory = title?.let { titleNotNull ->
                            { FText(titleNotNull) }
                        },
                        bottomAccessory = errorMessage?.let { message ->
                            { FText(message) }
                        },
                        content = content,
                    )
                }
            }

        }
    }
}

private interface ItemDrawer {

    @Composable
    fun Item(
        onClick: (() -> Unit)? = null,
        endAccessory: (@Composable () -> Unit)? = null,
        content: @Composable () -> Unit,
    )

    fun interface Factory {

        fun createItemDrawer(
            title: String?,
        ): ItemDrawer
    }
}

private sealed interface SInputContentDrawer {

    data class WithTitle(
        val content: @Composable (title: String, item: ItemDrawer) -> Unit,
    ) : SInputContentDrawer

    data class WithoutTitle(
        val content: @Composable (item: ItemDrawer) -> Unit,
    ) : SInputContentDrawer

    companion object
}

private fun SInputContentDrawer.Companion.flag(
    type: SInputType.Flag,
    state: MutableStateFlow<Boolean>,
    enabled: Boolean,
): SInputContentDrawer = SInputContentDrawer.WithTitle { title, itemDrawer ->
    var isChecked by state.collectAsMutableAccessor()
    itemDrawer.Item(
        onClick = enabled.ifTrue { { isChecked = !isChecked } },
        endAccessory = {
            FCheckBox(
                isChecked = isChecked
            )
        }
    ) {
        FText(title)
    }
}

private fun <E, V> SInputContentDrawer.Companion.edit(
    type: SInputType.Edit<E, V>,
    state: MutableStateFlow<String>,
    enabled: Boolean,
): SInputContentDrawer = SInputContentDrawer.WithoutTitle { itemDrawer ->
    itemDrawer.Item(
        endAccessory = state
            .collectAsState()
            .value
            .isNotEmpty()
            .and(enabled)
            .ifTrue {
                {
                    FIcon(
                        drawable = Drawable.Vector(Icons.Default.Cancel),
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { state.value = "" }
                    )
                }
            }
    ) {
        FTextField(
            value = state,
            keyboardOptions = KeyboardOptions(
                capitalization = type.type.config.capitalization,
                imeAction = type.imeAction,
                keyboardType = type.type.config.keyboardType,
            ),
            lineLimits = TextFieldLineLimits.SingleLine,
            enabled = enabled,
        )
    }
}