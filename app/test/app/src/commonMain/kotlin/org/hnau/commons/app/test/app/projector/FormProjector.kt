package org.hnau.commons.app.test.app.projector

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.CropDin
import androidx.compose.material.icons.filled.Earbuds
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.app.projector.fractal.semantic.SContentWithActions
import org.hnau.commons.app.projector.fractal.semantic.SElements
import org.hnau.commons.app.projector.fractal.semantic.SScreen
import org.hnau.commons.app.projector.fractal.semantic.input.InputProjector
import org.hnau.commons.app.projector.fractal.semantic.input.toUiInputStateHolder
import org.hnau.commons.app.projector.fractal.semantic.input.type.toInputProjectorPrototype
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.app.test.app.model.FormModel
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress

class FormProjector(
    scope: CoroutineScope,
    model: FormModel,
) {

    private val flag: InputProjector = model
        .flag
        .toUiInputStateHolder()
        .toInputProjectorPrototype()
        .createInputProjector(
            scope = scope,
            title = "Flag",
            icon = Drawable.Vector(Icons.Default.Mood),
        )


    private val decimal: InputProjector = model
        .decimal
        .toUiInputStateHolder { state, _ ->
            "Unable parse '$state' to BigDecimal"
        }
        .toInputProjectorPrototype()
        .createInputProjector(
            scope = scope,
            title = "Decimal",
            icon = Drawable.Vector(Icons.Default.CropDin),
        )


    private val integer: InputProjector = model
        .integer
        .toUiInputStateHolder { state, _ ->
            "Unable parse '$state' to BigInteger"
        }
        .toInputProjectorPrototype()
        .createInputProjector(
            scope = scope,
            title = "Integer",
            icon = Drawable.Vector(Icons.Default.Earbuds),
        )

    private val text = model
        .text
        .toUiInputStateHolder { state, error ->
            "String '$state' is too short: expected at least ${error.expectedMinLength} characters, got ${error.actualLength}"
        }
        .toInputProjectorPrototype()
        .createInputProjector(
            scope = scope,
            title = "Text",
            icon = Drawable.Vector(Icons.Default.Chair),
        )

    @Composable
    fun Content(
        contentPadding: PaddingValues,
    ) {
        SScreen(
            contentPadding = contentPadding,
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