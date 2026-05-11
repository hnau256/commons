package org.hnau.commons.app.projector.fractal.semantic.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.projector.fractal.FIcon
import org.hnau.commons.app.projector.fractal.FText
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.semantic.SItem
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.fractal.utils.Saturation
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.kotlin.coroutines.flow.state.mapState
import org.hnau.commons.kotlin.foldNullable

@JvmName("createProjectorWithError")
fun <S, E, V, I : InputType<S, E, V>> SInputProjectorFactory<S, E, V, I>.createProjector(
    title: String,
    icon: Drawable?,
    errorToMessage: (S, E) -> String,
): SInputProjector<S, E, V, I> = SInputProjector(
    title = title,
    icon = icon,
    factory = this,
    errorToMessage = errorToMessage,
)

@JvmName("createProjectorWithoutError")
fun <S, V, I : InputType<S, Nothing, V>> SInputProjectorFactory<S, Nothing, V, I>.createProjector(
    title: String,
    icon: Drawable?,
): SInputProjector<S, Nothing, V, I> = SInputProjector(
    title = title,
    icon = icon,
    factory = this,
    errorToMessage = { _, _ -> "" },
)

class SInputProjector<S, E, V, I : InputType<S, E, V>>(
    private val title: String,
    icon: Drawable?,
    private val factory: SInputProjectorFactory<S, E, V, I>,
    errorToMessage: (S, E) -> String,
) {

    private val itemDrawer: ItemDrawer = object : ItemDrawer {

        private val errorMessage: StateFlow<String?> = factory.model.stateWithValueOrError.mapState(
            scope = factory.scope
        ) { stateWithValueOrError ->
            stateWithValueOrError
                .value
                .mapLeft { error ->
                    val state = stateWithValueOrError.key
                    errorToMessage(state, error)
                }
                .leftOrNull()
        }

        private val topAccessoryTitle: String? = factory
            .contentProjector
            .fold(
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
        factory
            .contentProjector
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
