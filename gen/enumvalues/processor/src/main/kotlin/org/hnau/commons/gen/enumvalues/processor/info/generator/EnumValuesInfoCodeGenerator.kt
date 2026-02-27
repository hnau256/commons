package org.hnau.commons.gen.enumvalues.processor.info.generator

import com.google.devtools.ksp.getVisibility
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.toKModifier
import org.hnau.commons.gen.enumvalues.processor.info.EnumValuesInfo
import org.hnau.commons.gen.enumvalues.processor.info.generator.utils.enumClassName
import org.hnau.commons.gen.enumvalues.processor.info.generator.utils.identifier
import org.hnau.commons.gen.enumvalues.processor.info.generator.utils.valuesClassName


fun EnumValuesInfo.toTypeSpec(): TypeSpec = TypeSpec
    .classBuilder(valuesClass)
    .apply {
        modifiers += KModifier.DATA
        enumClass.getVisibility().toKModifier()?.let { modifiers += it }

        if (serializable) {
            annotations += AnnotationSpec
                .builder(ClassName("kotlinx.serialization", "Serializable"))
                .build()
        }

        val contentType = TypeVariableName(
            name = "T",
            variance = KModifier.OUT,
        )

        typeVariables += contentType

        primaryConstructor(
            FunSpec
                .constructorBuilder()
                .apply {
                    entries.forEach { entry ->
                        addParameter(
                            ParameterSpec
                                .builder(
                                    name = entry.identifier,
                                    type = contentType,
                                )
                                .build()
                        )
                    }
                }
                .build()
        )

        entries.forEach { entry ->
            propertySpecs += PropertySpec
                .builder(
                    name = entry.identifier,
                    type = contentType,
                )
                .initializer(entry.identifier)
                .build()
        }

        addFunction(
            toGetFuncSpec(
                contentType = contentType,
            )
        )

        addFunction(
            toFullMapFuncSpec(
                contentType = contentType,
            )
        )

        addFunction(
            toMapFuncSpec(
                contentType = contentType,
            )
        )

        addFunction(
            toFullCombineFuncSpec(
                contentType = contentType,
            )
        )

        addFunction(
            toCombineFuncSpec(
                contentType = contentType,
            )
        )

        addType(
            toCompanionObjectSpec()
        )
    }
    .build()

private fun EnumValuesInfo.toGetFuncSpec(
    contentType: TypeVariableName,
): FunSpec = FunSpec
    .builder("get")
    .apply {
        modifiers += KModifier.OPERATOR
        returns(contentType)
        addParameter(
            ParameterSpec
                .builder(
                    name = enumIdentifier,
                    type = enumClassName,
                )
                .build()
        )
        addCode(
            format = entries.joinToString(
                prefix = "return when ($enumIdentifier) {\n",
                postfix = "\n}",
                separator = "\n",
            ) { entry ->
                "\t%T.${entry.name} -> ${entry.identifier}"
            },
            args = entries
                .map { enumClassName }
                .toTypedArray(),
        )
    }
    .build()



private fun EnumValuesInfo.toFullMapFuncSpec(
    contentType: TypeVariableName,
): FunSpec = FunSpec
    .builder("map")
    .apply {
        modifiers += KModifier.INLINE

        val returnType = TypeVariableName(
            name = "R",
        )
        typeVariables += returnType

        returns(valuesClassName.parameterizedBy(returnType))

        addParameter(
            ParameterSpec
                .builder(
                    name = "transform",
                    type = LambdaTypeName.get(
                        parameters = arrayOf(
                            ParameterSpec(
                                name = enumIdentifier,
                                type = enumClassName,
                            ),
                            ParameterSpec(
                                name = "value",
                                type = contentType,
                            ),
                        ),
                        returnType = returnType,
                    ),
                )
                .build()
        )
        addCode(
            format = entries.joinToString(
                prefix = "return %T(\n",
                postfix = "\n)",
                separator = "\n",
            ) { entry ->
                "\t${entry.identifier} = transform(%T.${entry.name}, ${entry.identifier}),"
            },
            args = buildList {
                add(valuesClassName)
                addAll(entries.map { enumClassName })
            }
                .toTypedArray(),
        )
    }
    .build()

private fun EnumValuesInfo.toMapFuncSpec(
    contentType: TypeVariableName,
): FunSpec = FunSpec
    .builder("map")
    .apply {
        modifiers += KModifier.INLINE

        val returnType = TypeVariableName(
            name = "R",
        )
        typeVariables += returnType

        returns(valuesClassName.parameterizedBy(returnType))

        addParameter(
            ParameterSpec
                .builder(
                    name = "transform",
                    type = LambdaTypeName.get(
                        parameters = arrayOf(
                            ParameterSpec(
                                name = "value",
                                type = contentType,
                            ),
                        ),
                        returnType = returnType,
                    ),
                )
                .build()
        )
        addCode(
            "return map { _, value -> transform(value) }",
        )
    }
    .build()

private fun EnumValuesInfo.toFullCombineFuncSpec(
    contentType: TypeVariableName,
): FunSpec = FunSpec
    .builder("combineWith")
    .apply {
        modifiers += KModifier.INLINE

        val otherType = TypeVariableName(
            name = "O",
        )
        typeVariables += otherType

        val returnType = TypeVariableName(
            name = "R",
        )
        typeVariables += returnType

        returns(valuesClassName.parameterizedBy(returnType))

        addParameter(
            ParameterSpec
                .builder(
                    name = "other",
                    type = valuesClassName.parameterizedBy(otherType),
                )
                .build()
        )

        addParameter(
            ParameterSpec
                .builder(
                    name = "combine",
                    type = LambdaTypeName.get(
                        parameters = arrayOf(
                            ParameterSpec(
                                name = enumIdentifier,
                                type = enumClassName,
                            ),
                            ParameterSpec(
                                name = "value",
                                type = contentType,
                            ),
                            ParameterSpec(
                                name = "other",
                                type = otherType,
                            ),
                        ),
                        returnType = returnType,
                    ),
                )
                .build()
        )

        addCode(
            format = entries.joinToString(
                prefix = "return %T(\n",
                postfix = "\n)",
                separator = "\n",
            ) { entry ->
                "\t${entry.identifier} = combine(%T.${entry.name}, ${entry.identifier}, other.${entry.identifier}),"
            },
            args = buildList {
                add(valuesClassName)
                addAll(entries.map { enumClassName })
            }
                .toTypedArray(),
        )
    }
    .build()

private fun EnumValuesInfo.toCombineFuncSpec(
    contentType: TypeVariableName,
): FunSpec = FunSpec
    .builder("combineWith")
    .apply {
        modifiers += KModifier.INLINE

        val otherType = TypeVariableName(
            name = "O",
        )
        typeVariables += otherType

        val returnType = TypeVariableName(
            name = "R",
        )
        typeVariables += returnType

        returns(valuesClassName.parameterizedBy(returnType))

        addParameter(
            ParameterSpec
                .builder(
                    name = "other",
                    type = valuesClassName.parameterizedBy(otherType),
                )
                .build()
        )

        addParameter(
            ParameterSpec
                .builder(
                    name = "combine",
                    type = LambdaTypeName.get(
                        parameters = arrayOf(
                            ParameterSpec(
                                name = "value",
                                type = contentType,
                            ),
                            ParameterSpec(
                                name = "other",
                                type = otherType,
                            ),
                        ),
                        returnType = returnType,
                    ),
                )
                .build()
        )
        addCode(
            "return combineWith(other = other) { _, value, other -> combine(value, other) }",
        )
    }
    .build()


private fun EnumValuesInfo.toCompanionObjectSpec(): TypeSpec = TypeSpec
    .companionObjectBuilder()
    .addFunction(toCreateFuncSpec())
    .build()


private fun EnumValuesInfo.toCreateFuncSpec(): FunSpec = FunSpec
.builder("create")
.apply {
    modifiers += KModifier.INLINE

    val returnType = TypeVariableName(
        name = "T",
    )
    typeVariables += returnType

    returns(valuesClassName.parameterizedBy(returnType))

    addParameter(
        ParameterSpec
            .builder(
                name = "createValue",
                type = LambdaTypeName.get(
                    parameters = arrayOf(
                        ParameterSpec(
                            name = enumIdentifier,
                            type = enumClassName,
                        ),
                    ),
                    returnType = returnType,
                ),
            )
            .build()
    )
    addCode(
        format = entries.joinToString(
            prefix = "return %T(\n",
            postfix = "\n)",
            separator = "\n",
        ) { entry ->
            "\t${entry.identifier} = createValue(%T.${entry.name}),"
        },
        args = buildList {
            add(valuesClassName)
            addAll(entries.map { enumClassName })
        }
            .toTypedArray(),
    )
}
.build()

/*

companion object {

    inline fun <T> create(
        createValue: (amountDirection: AnalyticsTab) -> T,
    ): AnalyticsTabValues<T> = AnalyticsTabValues(
        accounts = createValue(AnalyticsTab.Accounts),
        graph = createValue(AnalyticsTab.Graph),
    )
}*/
