package org.hnau.commons.app.projector.fractal.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.flow.StateFlow
import org.hnau.commons.app.projector.fractal.SCell
import org.hnau.commons.app.projector.fractal.SIcon
import org.hnau.commons.app.projector.fractal.SItem
import org.hnau.commons.app.projector.fractal.SText
import org.hnau.commons.app.projector.fractal.context.LocalFContext
import org.hnau.commons.app.projector.fractal.context.UpdateFContext
import org.hnau.commons.app.projector.fractal.context.containerColor
import org.hnau.commons.app.projector.fractal.size.units
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.fractal.utils.Saturation
import org.hnau.commons.app.projector.uikit.table.TableScope
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.app.projector.utils.clickableOption
import org.hnau.commons.kotlin.foldNullable

class InputProjector(
    private val title: String,
    private val icon: Drawable?,
    contentProjector: InputContentProjector,
    mood: Mood,
    private val errorMessage: StateFlow<String?>,
) {

    private val itemTitleWithContent: Pair<String?, @Composable TableScope.(ItemDrawer) -> Unit> =
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
        override fun TableScope.Item(
            onClick: (() -> Unit)?,
            endAccessory: @Composable (() -> Unit)?,
            isFocused: Boolean,
            content: @Composable (() -> Unit)
        ) {

            val errorMessage by errorMessage.collectAsState()
            SCell { cellModifier, shape ->
                UpdateFContext(
                    update = {
                        errorMessage.foldNullable(
                            ifNull = {
                                copy(
                                    mood = mood,
                                    saturation = Saturation.get(isFocused),
                                )
                            },
                            ifNotNull = {
                                copy(
                                    mood = Mood.Error,
                                    saturation = Saturation.Active,
                                )
                            }
                        )
                    }
                ) {
                    val fContext = LocalFContext.current
                    Box(
                        modifier = cellModifier
                            .clip(shape)
                            .clickableOption(
                                onClick = onClick,
                            )
                            .background(
                                color = fContext.containerColor,
                            )
                            .padding(
                                fContext.distance.units.paddingValues.horizontal.medium,
                            ),
                        propagateMinConstraints = true,
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
    }

    @Composable
    fun TableScope.Content() {
        with(itemTitleWithContent) {
            second(itemDrawer)
        }
    }
}


