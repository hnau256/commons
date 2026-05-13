package org.hnau.commons.app.test.app.projector

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.CropDin
import androidx.compose.material.icons.filled.Earbuds
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.ImeAction
import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.app.projector.fractal.semantic.SContentWithActions
import org.hnau.commons.app.projector.fractal.semantic.SElements
import org.hnau.commons.app.projector.fractal.semantic.SScreen
import org.hnau.commons.app.projector.fractal.semantic.input.InputProjector
import org.hnau.commons.app.projector.fractal.semantic.input.createInputProjector
import org.hnau.commons.app.projector.fractal.semantic.input.type.toInputProjectorPrototype
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.app.test.app.model.FormModel
import org.hnau.commons.kotlin.coroutines.ActionOrElse
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress

class FormProjector(
    scope: CoroutineScope,
    model: FormModel,
) {

    private val flag: InputProjector = model
        .flag
        .toInputProjectorPrototype()
        .createInputProjector(
            scope = scope,
            title = "Flag",
            icon = Drawable.Vector(Icons.Default.Mood),
        )


    private val decimal: InputProjector = model
        .decimal
        .toInputProjectorPrototype(
            imeAction = ImeAction.Next,
        )
        .createInputProjector(
            scope = scope,
            title = "Decimal",
            icon = Drawable.Vector(Icons.Default.CropDin),
        ) { state, _ ->
            "Unable parse '$state' to BigDecimal"
        }


    private val integer: InputProjector = model
        .integer
        .toInputProjectorPrototype(
            imeAction = ImeAction.Next,
        )
        .createInputProjector(
            scope = scope,
            title = "Integer",
            icon = Drawable.Vector(Icons.Default.Earbuds),
        ) { state, _ ->
            "Unable parse '$state' to BigInteger"
        }

    private val text = model
        .text
        .toInputProjectorPrototype(
            imeAction = ImeAction.Done,
        )
        .createInputProjector(
            scope = scope,
            title = "Text",
            icon = Drawable.Vector(Icons.Default.Chair),
        ) { state, error ->
            "String '$state' is too short: expected at least ${error.expectedMinLength} characters, got ${error.actualLength}"
        }

    @Composable
    fun Content(
        contentPadding: PaddingValues,
    ) {
        SScreen(
            contentPadding = contentPadding,
            actions = {
                Action(
                    actionOrElseOrDisabled = ActionOrElse.Else(CancelOrInProgress.InProgress),
                    titleOrIcon = TitleOrIcon.Icon(Drawable.Vector(Icons.Default.Factory))
                )
            }
        ) {
            SContentWithActions(
                content = {
                    SElements {
                        flag.Content()
                        decimal.Content()
                        integer.Content()
                        text.Content()
                    }
                },
                actions = {
                    Action<CancelOrInProgress.Cancel>(
                        actionOrElseOrDisabled = null,
                        titleOrIcon = TitleOrIcon.Both(
                            title = "Save",
                            icon = Drawable.Vector(Icons.Default.Save),
                        )
                    )
                }
            )
        }
    }

}