package org.hnau.commons.app.test.app.model

import arrow.core.plus
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import org.hnau.commons.app.model.input.InputModel
import org.hnau.commons.app.model.input.InputParser
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.model.input.plus
import org.hnau.commons.app.model.input.simplify
import org.hnau.commons.app.model.input.skeleton.EditDecimalInputSkeleton
import org.hnau.commons.app.model.input.skeleton.EditIntegerInputSkeleton
import org.hnau.commons.app.model.input.skeleton.EditTextInputSkeleton
import org.hnau.commons.app.model.input.skeleton.FlagInputSkeleton
import org.hnau.commons.app.model.input.skeleton.StringIsTooShort
import org.hnau.commons.app.model.input.skeleton.UnableParseStringToDecimalError
import org.hnau.commons.app.model.input.skeleton.UnableParseStringToIntegerError
import org.hnau.commons.app.model.input.skeleton.createStringMinLengthValidator
import org.hnau.commons.app.model.utils.Editable
import org.hnau.commons.app.model.utils.combineEditableWith

class FormModel(
    scope: CoroutineScope,
    skeleton: Skeleton,
) {

    @Serializable
    data class Skeleton(
        val flag: FlagInputSkeleton,
        val decimal: EditDecimalInputSkeleton,
        val integer: EditIntegerInputSkeleton,
        val text: EditTextInputSkeleton,
    ) {

        constructor(
            initial: Config,
        ) : this(
            flag = FlagInputSkeleton(initial.flag),
            decimal = EditDecimalInputSkeleton(initial.decimal),
            integer = EditIntegerInputSkeleton(initial.integer),
            text = EditTextInputSkeleton(initial.text),
        )
    }


    val flag: InputModel<Boolean, Nothing, Boolean, InputType.Flag> = skeleton
        .flag
        .toModelFactory()
        .createInputModel(scope)


    val decimal: InputModel<String, UnableParseStringToDecimalError, BigDecimal, InputType.Edit> =
        skeleton
            .decimal
            .toModelFactory()
            .createInputModel(scope)


    val integer: InputModel<String, UnableParseStringToIntegerError, BigInteger, InputType.Edit> =
        skeleton.integer.toModelFactory()
            .createInputModel(scope)


    val text: InputModel<String, StringIsTooShort, String, InputType.Edit> =
        skeleton.text.toModelFactory {
            (it + InputParser.createStringMinLengthValidator(3)).simplify()
        }
            .createInputModel(scope)

    val config: StateFlow<Editable<Config>> = flag
        .editable
        .combineEditableWith(
            scope = scope,
            other = decimal.editable,
            combine = ::Pair,
        )
        .combineEditableWith(
            scope = scope,
            other = integer.editable,
            combine = Pair<Boolean, BigDecimal>::plus,
        )
        .combineEditableWith(
            scope = scope,
            other = text.editable,
        ) { (flag, decimal, integer), text ->
            Config(
                flag = flag,
                decimal = decimal,
                integer = integer,
                text = text,
            )
        }
}