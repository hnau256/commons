package org.hnau.commons.gen.sealup.processor.sealedinfo.generator

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.TypeVariableName
import org.hnau.commons.gen.sealup.processor.sealedinfo.SealedInfo
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.className
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.uppercasedIdentifier
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.visibility
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.wrapperClassName
import org.hnau.commons.kotlin.ifFalse

fun SealedInfo.toFoldFuncSpec(): FunSpec {
    val resultType = TypeVariableName("R")
    return FunSpec
        .builder("fold")
        .apply {
            modifiers += KModifier.INLINE
            visibility?.let { modifiers += it }

            typeVariables += resultType

            receiver(className)
            returns(resultType)

            variants.forEach { variant ->
                addParameter(
                    "if${variant.uppercasedIdentifier}",
                    LambdaTypeName.get(
                        parameters = listOfNotNull(
                            variant
                                .isObject
                                .ifFalse { variant.wrappedClassName }
                        ).toTypedArray(),
                        returnType = resultType,
                    ),
                )
            }
        }
        .addCode(
            variants.joinToString(
                prefix = "return when (this) {\n",
                postfix = "\n\t}",
                separator = "\n",
            ) { variant ->
                val argument = variant
                    .isObject
                    .ifFalse { variant.wrappedIdentifier }
                    .orEmpty()
                    .let { "($it)" }
                "\t\tis %T -> if${variant.uppercasedIdentifier}$argument"
            },
            args = variants
                .map { it.wrapperClassName(this@toFoldFuncSpec) }
                .toTypedArray(),
        )
        .build()
}
