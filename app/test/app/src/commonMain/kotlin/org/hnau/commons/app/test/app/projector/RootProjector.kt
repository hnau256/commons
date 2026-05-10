package org.hnau.commons.app.test.app.projector

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Lens
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.app.model.theme.palette.Palettes
import org.hnau.commons.app.projector.fractal.FBase
import org.hnau.commons.app.projector.fractal.FText
import org.hnau.commons.app.projector.fractal.context.FContext
import org.hnau.commons.app.projector.fractal.semantic.SContentWithActions
import org.hnau.commons.app.projector.fractal.semantic.SElements
import org.hnau.commons.app.projector.fractal.semantic.SMainWithAdditional
import org.hnau.commons.app.projector.fractal.semantic.input.SInput
import org.hnau.commons.app.projector.fractal.semantic.input.SInputMapper
import org.hnau.commons.app.projector.fractal.semantic.input.SInputState
import org.hnau.commons.app.projector.fractal.semantic.input.SInputType
import org.hnau.commons.app.projector.fractal.semantic.input.edit.SEditType
import org.hnau.commons.app.projector.fractal.semantic.input.edit.addMapper
import org.hnau.commons.app.projector.fractal.semantic.input.edit.createMinLengthValidator
import org.hnau.commons.app.projector.fractal.semantic.input.edit.type.decimal
import org.hnau.commons.app.projector.fractal.semantic.input.edit.type.integer
import org.hnau.commons.app.projector.fractal.semantic.input.edit.type.text
import org.hnau.commons.app.projector.fractal.size.SizeType
import org.hnau.commons.app.projector.fractal.utils.Mood
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.app.test.app.model.RootModel
import org.hnau.commons.gen.pipe.annotations.Pipe
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress
import org.hnau.commons.kotlin.coroutines.flow.state.mutable.toMutableStateFlowAsInitial

class RootProjector(
    scope: CoroutineScope,
    dependencies: Dependencies,
    private val model: RootModel,
) {

    @Pipe
    interface Dependencies {

        companion object
    }

    @Composable
    fun Content(
        palettes: Palettes,
        contentPadding: PaddingValues,
    ) {
        FBase(
            context = FContext(
                palettes = palettes,
            ),
            modifier = Modifier.fillMaxSize().padding(contentPadding),
        ) {
            SContentWithActions(
                modifier = Modifier.fillMaxSize(),
                content = {
                    SMainWithAdditional(
                        main = {
                            SElements {
                                FText("Main title", type = SizeType.Large)
                                FText("Main")

                                SInput(
                                    title = "Flag",
                                    icon = Drawable.Vector(Icons.Default.Computer),
                                    inputState = remember {
                                        SInputState
                                            .create(
                                                type = SInputType.Flag,
                                                initialValue = false,
                                            )
                                            .toMutableStateFlowAsInitial()
                                    },
                                )

                                SInput(
                                    title = "Decimal",
                                    icon = null,
                                    inputState = remember {
                                        SInputState
                                            .create(
                                                type = SInputType.Edit(
                                                    type = SEditType.decimal("Is not decimal"),
                                                ),
                                                initialValue = BigDecimal.fromFloat(123.345f),
                                            )
                                            .toMutableStateFlowAsInitial()
                                    },
                                )

                                SInput(
                                    title = "Integer",
                                    icon = Drawable.Vector(Icons.Default.Lens),
                                    inputState = remember {
                                        SInputState
                                            .create(
                                                type = SInputType.Edit(
                                                    type = SEditType.integer("Is not integer"),
                                                ),
                                                initialValue = BigInteger.fromInt(123),
                                            )
                                            .toMutableStateFlowAsInitial()
                                    },
                                )

                                SInput(
                                    title = "Text",
                                    icon = Drawable.Vector(Icons.Default.AddComment),
                                    inputState = remember {
                                        SInputState
                                            .create(
                                                type = SInputType.Edit(
                                                    type = SEditType.text().addMapper(
                                                        SInputMapper.createMinLengthValidator(
                                                            minLength = 3,
                                                            convertErrorToString = { error ->
                                                                "Expected at least ${error.minLength} symbols, got ${error.actualLength}"
                                                            }
                                                        )
                                                    ),
                                                ),
                                                initialValue = "qwerty",
                                            )
                                            .toMutableStateFlowAsInitial()
                                    },
                                )

                            }
                        },
                        additional = {
                            SContentWithActions(
                                content = { FText("Content") },
                                actions = {
                                    Action(
                                        actionOrElseOrDisabled = model.task.collectAsState().value,
                                        titleOrIcon = TitleOrIcon.Both(
                                            title = "Primary",
                                            icon = Drawable.Vector(Icons.Default.Settings),
                                        ),
                                        mood = Mood.Primary,
                                    )
                                    Action<CancelOrInProgress.Cancel>(
                                        actionOrElseOrDisabled = ActionOrElse.Action {},
                                        titleOrIcon = TitleOrIcon.Title("Secondary"),
                                        mood = Mood.Secondary,
                                    )
                                }
                            )
                        },
                    )
                },
                actions = {
                    Action(
                        actionOrElseOrDisabled = model.task.collectAsState().value,
                        titleOrIcon = TitleOrIcon.Both(
                            title = "Primary",
                            icon = Drawable.Vector(Icons.Default.Settings),
                        ),
                        mood = Mood.Primary,
                    )
                    Action<CancelOrInProgress.Cancel>(
                        actionOrElseOrDisabled = ActionOrElse.Action {},
                        titleOrIcon = TitleOrIcon.Title("Secondary"),
                        mood = Mood.Secondary,
                    )
                }
            )
        }

    }
}