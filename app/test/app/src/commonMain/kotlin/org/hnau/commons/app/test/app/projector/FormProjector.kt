package org.hnau.commons.app.test.app.projector

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.CropDin
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.runtime.Composable
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.model.input.skeleton.UnableParseStringToDecimalError
import org.hnau.commons.app.model.input.skeleton.UnableParseStringToIntegerError
import org.hnau.commons.app.projector.fractal.semantic.SContentWithActions
import org.hnau.commons.app.projector.fractal.semantic.SElements
import org.hnau.commons.app.projector.fractal.semantic.SScreen
import org.hnau.commons.app.projector.fractal.semantic.input.SInputProjector
import org.hnau.commons.app.projector.fractal.semantic.input.createProjector
import org.hnau.commons.app.projector.fractal.semantic.input.toContentProjector
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.app.projector.utils.TitleOrIcon
import org.hnau.commons.app.test.app.model.FormModel
import org.hnau.commons.kotlin.coroutines.CancelOrInProgress

class FormProjector(
    scope: CoroutineScope,
    model: FormModel,
) {

    private val flag: SInputProjector<Boolean, Nothing, Boolean, InputType.Flag> = model
        .flag
        .toContentProjector(scope)
        .createProjector(
            title = "Flag",
            icon = Drawable.Vector(Icons.Default.Mood),
        )


    private val decimal: SInputProjector<String, UnableParseStringToDecimalError, BigDecimal, InputType.Edit<UnableParseStringToDecimalError, BigDecimal>> =
        model
            .decimal
            .toContentProjector(scope)
            .createProjector(
                title = "Decimal",
                icon = Drawable.Vector(Icons.Default.CropDin),
                errorToMessage = { state, _ -> "Unable parse '$state' to BigDecimal" }
            )


    private val integer: SInputProjector<String, UnableParseStringToIntegerError, BigInteger, InputType.Edit<UnableParseStringToIntegerError, BigInteger>> =
        model
            .integer
            .toContentProjector(scope)
            .createProjector(
                title = "Integer",
                icon = Drawable.Vector(Icons.Default.SportsTennis),
                errorToMessage = { state, _ -> "Unable parse '$state' to BigInteger" }
            )

    private val text = model
        .text
        .toContentProjector(scope)
        .createProjector(
            title = "Text",
            icon = Drawable.Vector(Icons.Default.Chair),
            errorToMessage = { state, error ->
                "String '$state' is too short: expected at least ${error.expectedMinLength} characters, got ${error.actualLength}"
            }
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