package org.hnau.commons.app.projector.fractal.semantic.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.projector.fractal.FIcon
import org.hnau.commons.app.projector.fractal.FText
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.semantic.SItem
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.fractal.utils.Saturation
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.kotlin.foldNullable

class InputProjector(
    private val title: String,
    private val icon: Drawable?,
    contentProjector: InputContentProjector,
    private val errorMessage: StateFlow<String?>,
) {

    private val itemTitleWithContent: Pair<String?, @Composable ((ItemDrawer) -> Unit)> = contentProjector.fold(
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
                    topAccessory = itemTitleWithContent.first?.let { title ->
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
        itemTitleWithContent.second(itemDrawer)
    }
}


