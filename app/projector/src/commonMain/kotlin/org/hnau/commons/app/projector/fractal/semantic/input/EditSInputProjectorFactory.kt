package org.hnau.commons.app.projector.fractal.semantic.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.coroutines.CoroutineScope
import org.hnau.commons.app.model.input.InputModel
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.projector.fractal.FIcon
import org.hnau.commons.app.projector.fractal.FTextField
import org.hnau.commons.app.projector.utils.Drawable
import org.hnau.commons.kotlin.ifTrue

data class EditInputProjectorConfig(
    val keyboardType: KeyboardType,
    val capitalization: KeyboardCapitalization,
) {

    companion object {

        val text: EditInputProjectorConfig
            get() = EditInputProjectorConfig(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences,
            )

        val integer: EditInputProjectorConfig
            get() = EditInputProjectorConfig(
                keyboardType = KeyboardType.Number,
                capitalization = KeyboardCapitalization.None,
            )

        val decimal: EditInputProjectorConfig
            get() = EditInputProjectorConfig(
                keyboardType = KeyboardType.Decimal,
                capitalization = KeyboardCapitalization.None,
            )
    }
}

@JvmName("toIntegerProjector")
fun <E> InputModel<String, E, BigInteger, InputType.Edit<E, BigInteger>>.toContentProjector(
    scope: CoroutineScope,
    imeAction: ImeAction = ImeAction.Default,
): SInputProjectorFactory<String, E, BigInteger, InputType.Edit<E, BigInteger>> = toContentProjector(
    scope = scope,
    imeAction = imeAction,
    config = EditInputProjectorConfig.integer,
)

@JvmName("toDecimalProjector")
fun <E> InputModel<String, E, BigDecimal, InputType.Edit<E, BigDecimal>>.toContentProjector(
    scope: CoroutineScope,
    imeAction: ImeAction = ImeAction.Default,
): SInputProjectorFactory<String, E, BigDecimal, InputType.Edit<E, BigDecimal>> = toContentProjector(
    scope = scope,
    imeAction = imeAction,
    config = EditInputProjectorConfig.decimal,
)

@JvmName("toTextProjector")
fun <E> InputModel<String, E, String, InputType.Edit<E, String>>.toContentProjector(
    scope: CoroutineScope,
    imeAction: ImeAction = ImeAction.Default,
): SInputProjectorFactory<String, E, String, InputType.Edit<E, String>> = toContentProjector(
    scope = scope,
    imeAction = imeAction,
    config = EditInputProjectorConfig.text,
)

fun <E, V> InputModel<String, E, V, InputType.Edit<E, V>>.toContentProjector(
    scope: CoroutineScope,
    config: EditInputProjectorConfig,
    imeAction: ImeAction = ImeAction.Default,
): SInputProjectorFactory<String, E, V, InputType.Edit<E, V>> = toSInputProjectorFactory(
    scope = scope,
) {
    SInputContentProjector.WithoutTitle { itemDrawer ->
        val enabled by enabled.collectAsState()
        itemDrawer.Item(
            endAccessory = state
                .collectAsState()
                .value
                .isNotEmpty()
                .and(enabled)
                .ifTrue {
                    {
                        FIcon(
                            drawable = Drawable.Vector(Icons.Default.Cancel),
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { state.value = "" }
                        )
                    }
                }
        ) {
            FTextField(
                value = state,
                keyboardOptions = KeyboardOptions(
                    capitalization = config.capitalization,
                    imeAction = imeAction,
                    keyboardType = config.keyboardType,
                ),
                lineLimits = TextFieldLineLimits.SingleLine,
                enabled = enabled,
            )
        }
    }
}