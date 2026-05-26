@file:UseSerializers(
    BigDecimalSerializer::class,
    BigIntegerSerializer::class,
)

package org.hnau.commons.app.test.app.model

import arrow.core.plus
import arrow.core.toNonEmptyListOrThrow
import arrow.core.toNonEmptySetOrThrow
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.hnau.commons.app.model.goback.GoBackHandler
import org.hnau.commons.app.model.goback.NeverGoBackHandler
import org.hnau.commons.app.model.input.InputModel
import org.hnau.commons.app.model.input.InputParser
import org.hnau.commons.app.model.input.InputSkeleton
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.model.input.factory.InputModelFactory
import org.hnau.commons.app.model.input.factory.createModel
import org.hnau.commons.app.model.input.factory.createSkeletonForEdit
import org.hnau.commons.app.model.input.factory.type.StringIsTooShort
import org.hnau.commons.app.model.input.factory.type.UnableParseStringToDecimalError
import org.hnau.commons.app.model.input.factory.type.UnableParseStringToIntegerError
import org.hnau.commons.app.model.input.factory.type.createStringMinLengthValidator
import org.hnau.commons.app.model.input.factory.type.createVariant
import org.hnau.commons.app.model.input.factory.type.editDecimal
import org.hnau.commons.app.model.input.factory.type.editInteger
import org.hnau.commons.app.model.input.factory.type.editText
import org.hnau.commons.app.model.input.factory.type.flag
import org.hnau.commons.app.model.input.plus
import org.hnau.commons.app.model.utils.ModelSavableDelegate
import org.hnau.commons.app.model.utils.combineEditableWith
import org.hnau.commons.kotlin.it
import org.hnau.commons.kotlin.serialization.BigDecimalSerializer
import org.hnau.commons.kotlin.serialization.BigIntegerSerializer

class FormModel(
    scope: CoroutineScope,
    skeleton: Skeleton,
    goBack: () -> Unit,
    save: suspend (Config) -> Unit,
) {

    @Serializable
    data class Skeleton(
        val flag: InputSkeleton<Boolean, Boolean>,
        val decimal: InputSkeleton<String, BigDecimal>,
        val integer: InputSkeleton<String, BigInteger>,
        val text: InputSkeleton<String, String>,
        val variant: InputSkeleton<Config.Scheme, Config.Scheme>,
        val savableDelegate: ModelSavableDelegate.Skeleton<Config> = ModelSavableDelegate.Skeleton(),
    ) {

        constructor(
            initial: Config,
        ) : this(
            flag = InputModelFactory
                .flag
                .createSkeletonForEdit(initial.flag),

            decimal = InputModelFactory
                .editDecimal
                .createSkeletonForEdit(initial.decimal),

            integer = InputModelFactory
                .editInteger
                .createSkeletonForEdit(initial.integer),

            text = textInputModelFactory
                .createSkeletonForEdit(initial.text),

            variant = variantInputModelFactory
                .createSkeletonForEdit(initial.scheme),
        )
    }


    val flag: InputModel<Boolean, Nothing, Boolean, InputType.Flag> = InputModelFactory
        .flag
        .createModel(
            scope = scope,
            skeleton = skeleton.flag,
        )


    val decimal: InputModel<String, UnableParseStringToDecimalError, BigDecimal, InputType.Edit> =
        InputModelFactory
            .editDecimal
            .createModel(
                scope = scope,
                skeleton = skeleton.decimal,
            )


    val integer: InputModel<String, UnableParseStringToIntegerError, BigInteger, InputType.Edit> =
        InputModelFactory
            .editInteger
            .createModel(
                scope = scope,
                skeleton = skeleton.integer,
            )


    val text: InputModel<String, StringIsTooShort, String, InputType.Edit> =
        textInputModelFactory
            .createModel(
                scope = scope,
                skeleton = skeleton.text,
            )


    val variant: InputModel<Config.Scheme, Nothing, Config.Scheme, InputType.Variant<Config.Scheme>> =
        variantInputModelFactory
            .createModel(
                scope = scope,
                skeleton = skeleton.variant,
            )

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
                combine = Triple<Boolean, BigDecimal, BigInteger>::plus,
            )
            .combineEditableWith(
                scope = scope,
                other = variant.editable,
            ) { (flag, decimal, integer, text), variant ->
                Config(
                    flag = flag,
                    decimal = decimal,
                    integer = integer,
                    text = text,
                    scheme = variant,
                )
            },
        skeleton = skeleton.savableDelegate,
        modelGoBackHandler = NeverGoBackHandler,
        close = goBack,
        save = save,
    )

    val goBackHandler: GoBackHandler
        get() = savableDelegate.goBackHandler

    companion object {

        private val textInputModelFactory: InputModelFactory<String, StringIsTooShort, String, InputType.Edit> =
            InputModelFactory
                .editText(
                    encoder = ::it,
                ) { initialParser ->
                    initialParser + InputParser.createStringMinLengthValidator(3)
                }

        private val variantInputModelFactory: InputModelFactory<Config.Scheme, Nothing, Config.Scheme, InputType.Variant<Config.Scheme>> =
            InputModelFactory
                .createVariant(
                    variants = Config.Scheme.entries.toNonEmptyListOrThrow(),
                )
    }
}