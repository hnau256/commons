package org.hnau.commons.app.projector.fractal.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.model.input.InputStateHolder.Decoration
import org.hnau.commons.app.model.input.fold
import org.hnau.commons.app.projector.fractal.SItem
import org.hnau.commons.app.projector.fractal.SPanel
import org.hnau.commons.app.projector.fractal.SText
import org.hnau.commons.app.projector.fractal.context.FContext
import org.hnau.commons.app.projector.fractal.utils.Importance
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.fractal.utils.activate
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress
import org.hnau.commons.kotlin.coroutines.instant
import org.hnau.commons.kotlin.coroutines.noAction
import org.hnau.commons.kotlin.foldNullable
import org.hnau.commons.kotlin.ifTrue

class InputProjector(
    private val title: String,
    contentProjector: InputContentProjector,
    private val startAccessory: (@Composable () -> Unit)? = null,
    importanceToActivate: Importance? = Importance.default,
    titleMaxLines: Int = 1,
    private val decoration: StateFlow<Decoration?>,
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
                    val decoration by decoration
                        .collectAsState()
                    SPanel(
                        actionOrElseOrDisabled = decoration.foldNullable(
                            ifNull = { ActionOrElse.instant(onClick) },
                            ifNotNull = { decoration ->
                                decoration.fold(
                                    ifInProgress = {
                                        ActionOrElse.Else(
                                            cancelOrInProgress = when (onClick) {
                                                null -> CancelOrInProgress.InProgress
                                                else -> CancelOrInProgress.Cancel(onClick)
                                            } //TODO replace with foldNullable
                                        )
                                    },
                                    ifSelected = { ActionOrElse.noAction },
                                )
                            }
                        ),
                        isSelected = decoration
                            ?.fold(
                                ifSelected = { true },
                                ifInProgress = { false },
                            )
                            ?: false,
                        importanceToActivate = null,
                    ) {
                        SItem(
                            startAccessory = startAccessory,
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


