package org.hnau.commons.app.projector.fractal.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.projector.fractal.SIcon
import org.hnau.commons.app.projector.fractal.SItem
import org.hnau.commons.app.projector.fractal.SPanel
import org.hnau.commons.app.projector.fractal.SText
import org.hnau.commons.app.projector.fractal.context.FContext
import org.hnau.commons.app.projector.fractal.utils.Importance
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.fractal.utils.activate
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.instant
import org.hnau.commons.kotlin.foldBoolean
import org.hnau.commons.kotlin.foldNullable
import org.hnau.commons.kotlin.ifTrue

class InputProjector(
    private val title: String,
    private val icon: Drawable?,
    contentProjector: InputContentProjector,
    importanceToActivate: Importance? = Importance.default,
    titleMaxLines: Int = 1,
    private val errorMessage: StateFlow<String?>,
) {

    private val itemTitleWithContent: Pair<String?, @Composable (ItemDrawer) -> Unit> =
        contentProjector.fold(
            ifWithTitle = { content ->
                null to @Composable { itemDrawer: ItemDrawer ->
                    content(title, titleMaxLines, itemDrawer)
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
            isFocused: Boolean,
            content: @Composable (() -> Unit)
        ) {

            val errorMessage by errorMessage.collectAsState()
            FContext(
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
                FContext(
                    update = {
                        copy(
                            mood = isFocused
                                .ifTrue { importanceToActivate }
                                .foldNullable(
                                    ifNull = { mood },
                                    ifNotNull = mood::activate,
                                ),
                        )
                    }
                ) {
                    SPanel(
                        actionOrElseOrDisabled = ActionOrElse.instant(onClick),
                        importanceToActivate = null,
                    ) {
                        SItem(
                            startAccessory = icon?.let { iconNotNull ->
                                { SIcon(iconNotNull) }
                            },
                            endAccessory = endAccessory,
                            topAccessory = itemTitleWithContent.first?.let { title ->
                                {
                                    SText(
                                        text = title,
                                        maxLines = titleMaxLines,
                                    )
                                }
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
    }

    @Composable
    fun Content() {
        itemTitleWithContent.second(itemDrawer)
    }
}


