package org.hnau.commons.app.projector.fractal.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.projector.fractal.SIcon
import org.hnau.commons.app.projector.fractal.SItem
import org.hnau.commons.app.projector.fractal.SPanel
import org.hnau.commons.app.projector.fractal.SText
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.table.SCellScope
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.fractal.utils.Saturation
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.instant
import org.hnau.commons.kotlin.foldNullable

class InputProjector(
    private val title: String,
    private val icon: Drawable?,
    contentProjector: InputContentProjector,
    mood: Mood,
    private val errorMessage: StateFlow<String?>,
) {

    private val itemTitleWithContent: Pair<String?, @Composable SCellScope.(ItemDrawer) -> Unit> =
        contentProjector.fold(
            ifWithTitle = { content ->
                null to @Composable { itemDrawer: ItemDrawer ->
                    content(title, itemDrawer)
                }
            },
            ifWithoutTitle = { content ->
                title to content
            }
        )

    private val itemDrawer: ItemDrawer = object : ItemDrawer {


        @Composable
        override fun SCellScope.Item(
            onClick: (() -> Unit)?,
            endAccessory: @Composable (() -> Unit)?,
            isFocused: Boolean,
            content: @Composable (() -> Unit)
        ) {

            val errorMessage by errorMessage.collectAsState()
            UpdateFContext(
                update = {
                    errorMessage.foldNullable(
                        ifNull = {
                            copy(
                                mood = mood,
                            )
                        },
                        ifNotNull = {
                            copy(
                                mood = Mood.Error,
                            )
                        }
                    )
                }
            ) {
                SPanel(
                    actionOrElseOrDisabled = ActionOrElse.instant(onClick),
                    shape = shape,
                    saturation = Saturation.get(isFocused),
                ) {
                    SItem(
                        startAccessory = icon?.let { iconNotNull ->
                            { SIcon(iconNotNull) }
                        },
                        endAccessory = endAccessory,
                        topAccessory = itemTitleWithContent.first?.let { title ->
                            { SText(title) }
                        },
                        bottomAccessory = errorMessage?.let { message ->
                            { SText(message) }
                        },
                        content = content,
                    )
                }
            }
        }
    }

    @Composable
    fun SCellScope.Content() {
        with(itemTitleWithContent) {
            second(itemDrawer)
        }
    }
}


