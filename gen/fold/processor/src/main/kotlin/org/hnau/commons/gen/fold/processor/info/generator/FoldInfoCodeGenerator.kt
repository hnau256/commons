package org.hnau.commons.gen.fold.processor.info.generator

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import org.hnau.commons.gen.fold.processor.info.FoldInfo
import org.hnau.commons.gen.fold.processor.info.generator.utils.className
import org.hnau.commons.gen.fold.processor.info.generator.utils.isEnum
import org.hnau.commons.gen.fold.processor.info.generator.utils.nonEmptyEntriesPropertyName
import org.hnau.commons.gen.fold.processor.info.generator.utils.uppercasedIdentifier
import org.hnau.commons.gen.kotlin.codeBlock
import org.hnau.commons.kotlin.foldBoolean
import org.hnau.commons.kotlin.ifTrue

private val nonEmptyListClassName = ClassName("arrow.core", "NonEmptyList")

private val toNonEmptyListOrThrowMember = MemberName("arrow.core", "toNonEmptyListOrThrow")

private val suppressUncheckedCast: AnnotationSpec = AnnotationSpec
    .builder(Suppress::class)
    .addMember("%S", "UNCHECKED_CAST")
    .build()

private fun List<TypeVariableName>.resultTypeVariable(): TypeVariableName {
    val usedNames = mapTo(mutableSetOf()) { it.name }
    val name = generateSequence("R") { "${it}_fold" }
        .first { it !in usedNames }
    return TypeVariableName(name)
}

private fun FoldInfo.Variant.rawVariantType(): TypeName =
    (typeVariables.isNotEmpty() && resolution !is FoldInfo.Resolution.Object).foldBoolean(
        ifTrue = { className.parameterizedBy(*typeVariables.toTypedArray()) },
        ifFalse = { className },
    )

private fun FoldInfo.Variant.foldLambdaParameters(): List<ParameterSpec> =
    when (val resolution = resolution) {
        is FoldInfo.Resolution.Object -> emptyList()
        is FoldInfo.Resolution.Whole -> listOf(
            ParameterSpec.builder("value", resolution.type).build()
        )
        is FoldInfo.Resolution.Destructured -> resolution.parameters.map { param ->
            ParameterSpec.builder(param.name, param.type).build()
        }
    }

fun FoldInfo.toFoldRawFunSpec(): FunSpec {
    val classTypeVars = this.typeVariables
    val returnType = classTypeVars.resultTypeVariable()
    return FunSpec
        .builder("foldRaw")
        .apply {
            modifiers += KModifier.INLINE

            typeVariables += returnType
            classTypeVars.forEach { typeVariables += it }

            receiver(
                if (classTypeVars.isEmpty()) className
                else className.parameterizedBy(*classTypeVars.toTypedArray())
            )
            returns(returnType)

            variants.forEach { variant ->
                addParameter(
                    "if${variant.uppercasedIdentifier}",
                    LambdaTypeName.get(
                        parameters = arrayOf(
                            ParameterSpec.builder("variant", variant.rawVariantType()).build(),
                        ),
                        returnType = returnType,
                    ),
                )
            }
        }
        .addCode(
            codeBlock {
                variants.joinToString(
                    prefix = "return when (this) {\n",
                    postfix = "\n}",
                    separator = "\n",
                ) { variant ->
                    val left = if (isEnum) {
                        use(variant.className) + ".${variant.identifier}"
                    } else {
                        "is " + use(variant.className)
                    }
                    val right = "if${variant.uppercasedIdentifier}(this)"
                    "\t$left -> $right"
                }
            }
        )
        .build()
}

fun FoldInfo.toNonEmptyEntriesPropertySpecs(): List<PropertySpec> {
    if (!nonEmptyEntries) return emptyList()

    val nonEmptyListType = nonEmptyListClassName.parameterizedBy(className)

    val entriesProperty = PropertySpec
        .builder(
            name = nonEmptyEntriesPropertyName,
            type = nonEmptyListType,
        )
        .addModifiers(KModifier.PRIVATE)
        .initializer(
            "%T.entries.%M()",
            className,
            toNonEmptyListOrThrowMember,
        )
        .build()

    val companionProperty = PropertySpec
        .builder(
            name = "nonEmptyEntries",
            type = nonEmptyListType,
        )
        .receiver(className.nestedClass("Companion"))
        .getter(
            FunSpec
                .getterBuilder()
                .addCode("return %N", nonEmptyEntriesPropertyName)
                .build()
        )
        .build()

    return listOf(entriesProperty, companionProperty)
}

fun FoldInfo.toFoldFunSpec(): FunSpec {
    val classTypeVars = this.typeVariables
    val returnType = classTypeVars.resultTypeVariable()
    return FunSpec
        .builder("fold")
        .apply {
            modifiers += KModifier.INLINE

            typeVariables += returnType
            classTypeVars.forEach { typeVariables += it }

            receiver(
                if (classTypeVars.isEmpty()) className
                else className.parameterizedBy(*classTypeVars.toTypedArray())
            )
            returns(returnType)

            variants.forEach { variant ->
                addParameter(
                    "if${variant.uppercasedIdentifier}",
                    LambdaTypeName.get(
                        parameters = variant.foldLambdaParameters().toTypedArray(),
                        returnType = returnType,
                    ),
                )
            }
        }
        .addCode(
            codeBlock {
                variants.joinToString(
                    prefix = "return foldRaw(\n",
                    postfix = "\n)",
                    separator = ",\n",
                ) { variant ->
                    val name = "if${variant.uppercasedIdentifier}"
                    val content = when (val resolution = variant.resolution) {
                        is FoldInfo.Resolution.Object -> "$name()"
                        is FoldInfo.Resolution.Whole -> "$name(variant)"
                        is FoldInfo.Resolution.Destructured -> {
                            val args = resolution.parameters.joinToString(", ") { "variant.${it.name}" }
                            "$name($args)"
                        }
                    }
                    "\t$name = { variant -> $content }"
                }
            }
        )
        .build()
}

private fun FoldInfo.toDispatchFunSpec(
    name: String,
    raw: Boolean,
    resultTypeIndex: Int,
): FunSpec {
    val classTypeVars = this.typeVariables
    val resultType = classTypeVars[resultTypeIndex]
    val foldName = raw.foldBoolean(
        ifTrue = { "foldRaw" },
        ifFalse = { "fold" },
    )
    return FunSpec
        .builder(name)
        .apply {
            modifiers += KModifier.INLINE
            addAnnotation(suppressUncheckedCast)

            classTypeVars.forEach { typeVariables += it }

            receiver(className.parameterizedBy(*classTypeVars.toTypedArray()))
            returns(resultType)

            variants.forEach { variant ->
                val parameters = raw.foldBoolean(
                    ifTrue = {
                        arrayOf(ParameterSpec.builder("variant", variant.rawVariantType()).build())
                    },
                    ifFalse = {
                        variant.foldLambdaParameters().toTypedArray()
                    },
                )
                addParameter(
                    "if${variant.uppercasedIdentifier}",
                    LambdaTypeName.get(
                        parameters = parameters,
                        returnType = variant.dispatchResultType
                            ?: error("Dispatch result type is missing for variant ${variant.identifier}"),
                    ),
                )
            }
        }
        .addCode(
            codeBlock {
                val arguments = variants.joinToString(
                    separator = ",\n",
                ) { variant ->
                    val parameterName = "if${variant.uppercasedIdentifier}"
                    "\t$parameterName = $parameterName"
                }
                "return $foldName(\n$arguments\n) as ${resultType.name}"
            }
        )
        .build()
}

fun FoldInfo.toDispatchFunSpec(): FunSpec? = dispatchTypeParameterIndex?.let { index ->
    toDispatchFunSpec(name = "dispatch", raw = false, resultTypeIndex = index)
}

fun FoldInfo.toDispatchRawFunSpec(): FunSpec? = dispatchTypeParameterIndex?.let { index ->
    toDispatchFunSpec(name = "dispatchRaw", raw = true, resultTypeIndex = index)
}
