@file:UseSerializers(
    BigDecimalSerializer::class,
    BigIntegerSerializer::class,
)

package org.hnau.commons.app.test.app.model

import arrow.core.left
import arrow.core.plus
import arrow.core.right
import arrow.core.toNonEmptyListOrThrow
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.hnau.commons.app.model.goback.GoBackHandler
import org.hnau.commons.app.model.goback.NeverGoBackHandler
import org.hnau.commons.app.model.input.InputModel
import org.hnau.commons.app.model.input.InputSkeleton
import org.hnau.commons.app.model.input.InputType
import org.hnau.commons.app.model.input.factory.InputModelFactory
import org.hnau.commons.app.model.input.factory.createModel
import org.hnau.commons.app.model.input.factory.createSkeleton
import org.hnau.commons.app.model.input.factory.toInputModelFactory
import org.hnau.commons.app.model.input.parser.ParsingMapper
import org.hnau.commons.app.model.input.parser.createValidator
import org.hnau.commons.app.model.utils.ModelSavableDelegate
import org.hnau.commons.app.model.utils.combineEditableWith
import org.hnau.commons.kotlin.foldNullable
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
        val decimal: InputSkeleton<String, Float>,
        val integer: InputSkeleton<String, Int>,
        val text: InputSkeleton<String, String>,
        val variant: InputSkeleton<Config.Scheme, Config.Scheme>,
        val savableDelegate: ModelSavableDelegate.Skeleton<Config> = ModelSavableDelegate.Skeleton(),
    ) {

        constructor(
            initial: Config,
        ) : this(
            flag = flagInputModelFactory.createSkeleton(
                value = initial.flag,
                useValueAsInitial = true,
            ),

            decimal = decimalInputModelFactory.createSkeleton(
                value = initial.decimal,
                useValueAsInitial = true,
            ),

            integer = integerInputModelFactory.createSkeleton(
                value = initial.integer,
                useValueAsInitial = true,
            ),

            text = textInputModelFactory.createSkeleton(
                value = initial.text,
                useValueAsInitial = true,
            ),

            variant = variantInputModelFactory.createSkeleton(
                value = initial.scheme,
                useValueAsInitial = true,
            ),
        )
    }


    val flag: InputModel<Boolean, Boolean, Nothing, InputType.Flag> =
        flagInputModelFactory.createModel(
            scope = scope,
            skeleton = skeleton.flag,
        )


    val decimal: InputModel<String, Float, Unit, InputType.Edit> =
        decimalInputModelFactory.createModel(
            scope = scope,
            skeleton = skeleton.decimal,
        )


    val integer: InputModel<String, Int, Unit, InputType.Edit> =
        integerInputModelFactory.createModel(
            scope = scope,
            skeleton = skeleton.integer,
        )


    val text: InputModel<String, String, Unit, InputType.Edit> =
        textInputModelFactory.createModel(
            scope = scope,
            skeleton = skeleton.text,
        )


    val variant: InputModel<Config.Scheme, Config.Scheme, Nothing, InputType.Variant<Config.Scheme>> =
        variantInputModelFactory.createModel(
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
                combine = Pair<Boolean, Float>::plus,
            )
            .combineEditableWith(
                scope = scope,
                other = text.editable,
                combine = Triple<Boolean, Float, Int>::plus,
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

        private val flagInputModelFactory: InputModelFactory<Boolean, Boolean, Nothing, InputType.Flag> =
            InputType.Flag.toInputModelFactory()

        private val textInputModelFactory: InputModelFactory<String, String, Unit, InputType.Edit> =
            InputType.Edit.toInputModelFactory(
                parsingMapper = ParsingMapper.createValidator { string ->
                    string.takeIf { it.length >= 3 }.foldNullable(
                        ifNull = { Unit.left() },
                        ifNotNull = { Unit.right() }
                    )
                }
            )

        private val integerInputModelFactory: InputModelFactory<String, Int, Unit, InputType.Edit> =
            InputType.Edit.toInputModelFactory(
                ParsingMapper(
                    encode = Int::toString,
                    parse = { string ->
                        string
                            .toIntOrNull()
                            .foldNullable(
                                ifNull = { Unit.left() },
                                ifNotNull = Int::right,
                            )
                    }
                )
            )

        private val decimalInputModelFactory: InputModelFactory<String, Float, Unit, InputType.Edit> =
            InputType.Edit.toInputModelFactory(
                ParsingMapper(
                    encode = Float::toString,
                    parse = { string ->
                        string
                            .toFloatOrNull()
                            .foldNullable(
                                ifNull = { Unit.left() },
                                ifNotNull = Float::right,
                            )
                    }
                )
            )

        private val variantInputModelFactory: InputModelFactory<Config.Scheme, Config.Scheme, Nothing, InputType.Variant<Config.Scheme>> =
            InputType
                .Variant(
                    variants = Config.Scheme.entries.toNonEmptyListOrThrow(),
                )
                .toInputModelFactory()
    }
}