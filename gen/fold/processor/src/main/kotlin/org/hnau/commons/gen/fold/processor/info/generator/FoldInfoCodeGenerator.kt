package org.hnau.commons.gen.fold.processor.info.generator

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import org.hnau.commons.gen.fold.processor.info.FoldInfo
import org.hnau.commons.gen.fold.processor.info.generator.utils.className
import org.hnau.commons.gen.fold.processor.info.generator.utils.isEnum
import org.hnau.commons.gen.fold.processor.info.generator.utils.uppercasedIdentifier
import org.hnau.commons.gen.kotlin.codeBlock

fun FoldInfo.toFoldRawFunSpec(): FunSpec {
    val returnType = TypeVariableName("R")
    val classTypeVars = this.typeVariables
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
                val variantType: TypeName = if (classTypeVars.isNotEmpty() &&
                    variant.resolution !is FoldInfo.Resolution.Object
                ) {
                    variant.className.parameterizedBy(*classTypeVars.toTypedArray())
                } else {
                    variant.className
                }
                addParameter(
                    "if${variant.uppercasedIdentifier}",
                    LambdaTypeName.get(
                        parameters = arrayOf(
                            ParameterSpec.builder("variant", variantType).build(),
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

fun FoldInfo.toFoldFunSpec(): FunSpec {
    val returnType = TypeVariableName("R")
    val classTypeVars = this.typeVariables
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
                val lambdaParams = when (val resolution = variant.resolution) {
                    is FoldInfo.Resolution.Object -> emptyList()
                    is FoldInfo.Resolution.Whole -> listOf(
                        ParameterSpec.builder("value", resolution.type).build()
                    )
                    is FoldInfo.Resolution.Destructured -> resolution.parameters.map { param ->
                        ParameterSpec.builder(param.name, param.type).build()
                    }
                }

                addParameter(
                    "if${variant.uppercasedIdentifier}",
                    LambdaTypeName.get(
                        parameters = lambdaParams.toTypedArray(),
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
                    val content = when (val resolution = variant.resolution) {
                        is FoldInfo.Resolution.Object ->
                            "if${variant.uppercasedIdentifier}()"
                        is FoldInfo.Resolution.Whole ->
                            "if${variant.uppercasedIdentifier}(variant)"
                        is FoldInfo.Resolution.Destructured -> {
                            val args = resolution.parameters.joinToString(", ") { "variant.${it.name}" }
                            "if${variant.uppercasedIdentifier}($args)"
                        }
                    }
                    "\tif${variant.uppercasedIdentifier} = { variant -> $content }"
                }
            }
        )
        .build()
}
