@file:UseSerializers(
    BigDecimalSerializer::class,
    BigIntegerSerializer::class,
)

package org.hnau.commons.app.test.app.model

import arrow.core.plus
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import jdk.jfr.ContentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.hnau.commons.app.model.goback.GoBackHandler
import org.hnau.commons.app.model.goback.NeverGoBackHandler
import org.hnau.commons.app.model.input.InputModel
import org.hnau.commons.app.model.input.InputParser
import org.hnau.commons.app.model.input.InputSkeleton
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.model.input.parse.StringIsTooShort
import org.hnau.commons.app.model.input.parse.UnableParseStringToDecimalError
import org.hnau.commons.app.model.input.parse.UnableParseStringToIntegerError
import org.hnau.commons.app.model.input.parse.createStringMinLengthValidator
import org.hnau.commons.app.model.input.parse.stringToBigDecimal
import org.hnau.commons.app.model.input.parse.stringToBigInteger
import org.hnau.commons.app.model.input.plus
import org.hnau.commons.app.model.input.toModelPrototype
import org.hnau.commons.app.model.utils.ModelSavableDelegate
import org.hnau.commons.app.model.utils.combineEditableWith
import org.hnau.commons.kotlin.serialization.BigDecimalSerializer
import org.hnau.commons.kotlin.serialization.BigIntegerSerializer

class FormModel(
    scope: CoroutineScope,
    skeleton: Skeleton,
) {

    @Serializable
    data class Skeleton(
        val flag: InputSkeleton<Boolean, Boolean>,
        val decimal: InputSkeleton<String, BigDecimal>,
        val integer: InputSkeleton<String, BigInteger>,
        val text: InputSkeleton<String, String>,
        val savableDelegate: ModelSavableDelegate.Skeleton<Config> = ModelSavableDelegate.Skeleton(),
    ) {

        constructor(
            initial: Config,
        ) : this(
            flag = InputSkeleton.Factory<Boolean>()
                .createForEdit(initial.flag),

            decimal = InputSkeleton.Factory(BigDecimal::toStringExpanded)
                .createForEdit(initial.decimal),

            integer = InputSkeleton.Factory(BigInteger::toString)
                .createForEdit(initial.integer),

            text = InputSkeleton.Factory<String>()
                .createForEdit(initial.text),
        )
    }


    val flag: InputModel<Boolean, Nothing, Boolean, InputType.Flag> = skeleton
        .flag
        .toModelPrototype(InputType.Flag)
        .toInputModel(scope)


    val decimal: InputModel<String, UnableParseStringToDecimalError, BigDecimal, InputType.Edit> =
        skeleton
            .decimal
            .toModelPrototype(
                type = InputType.Edit(InputType.Edit.ContentType.Decimal),
                parser = InputParser.stringToBigDecimal,
            )
            .toInputModel(scope)


    val integer: InputModel<String, UnableParseStringToIntegerError, BigInteger, InputType.Edit> =
        skeleton
            .integer
            .toModelPrototype(
                type = InputType.Edit(InputType.Edit.ContentType.Integer),
                parser = InputParser.stringToBigInteger,
            )
            .toInputModel(scope)


    val text: InputModel<String, StringIsTooShort, String, InputType.Edit> =
        skeleton
            .text
            .toModelPrototype(
                type = InputType.Edit(InputType.Edit.ContentType.Text),
                parser = InputParser.createStringMinLengthValidator(3)
            )
            .toInputModel(scope)

    val savableDelegate: ModelSavableDelegate<Config> = ModelSavableDelegate(
        scope = scope,
        result = flag
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
            },
        skeleton = skeleton.savableDelegate,
        modelGoBackHandler = NeverGoBackHandler,
        close = {},
        save = {},
    )

    val goBackHandler: GoBackHandler
        get() = savableDelegate.goBackHandler
}