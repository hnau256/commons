package org.hnau.commons.gen.fold.processor.info.generator

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeVariableName
import org.hnau.commons.gen.fold.processor.info.FoldInfo
import org.hnau.commons.gen.fold.processor.info.generator.utils.className
import org.hnau.commons.gen.fold.processor.info.generator.utils.isEnum
import org.hnau.commons.gen.fold.processor.info.generator.utils.uppercasedIdentifier
import org.hnau.commons.gen.kotlin.codeBlock

fun FoldInfo.toFoldFunSpec(): FunSpec {
    val returnType = TypeVariableName("R")
    return FunSpec
        .builder("fold")
        .apply {
            modifiers += KModifier.INLINE

            typeVariables += returnType

            receiver(className)
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
                    prefix = "return when (this) {\n",
                    postfix = "\n}",
                    separator = "\n",
                ) { variant ->
                    val left = if (isEnum) {
                        use(variant.className) + ".${variant.identifier}"
                    } else {
                        "is " + use(variant.className)
                    }

                    val args = when (val resolution = variant.resolution) {
                        is FoldInfo.Resolution.Object -> ""
                        is FoldInfo.Resolution.Whole -> "value"
                        is FoldInfo.Resolution.Destructured -> resolution.parameters.joinToString(", ") { it.name }
                    }

                    val right = "if${variant.uppercasedIdentifier}($args)"

                    "\t$left -> $right"
                }
            }
        )
        .build()
}
