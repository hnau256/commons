package org.hnau.commons.gen.sealup.processor.sealedinfo.generator

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.TypeVariableName
import org.hnau.commons.gen.kotlin.codeBlock
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
                                .ifFalse { variant.wrapped.className }
                        ).toTypedArray(),
                        returnType = resultType,
                    ),
                )
            }
        }
        .addCode(
            CodeBlock
                .builder()
                .apply {

                }
                .build()
        )
        .addCode(
            codeBlock {
                variants.joinToString(
                    prefix = "return when (this) {\n",
                    postfix = "\n\t}",
                    separator = "\n",
                ) { variant ->

                    val wrapper = variant.wrapperClassName(this@toFoldFuncSpec)
                    val left = "is ${use(wrapper)}"

                    val argument = when (val pointer = variant.wrapped.pointer) {
                        is SealedInfo.Variant.Wrapped.Pointer.Class -> pointer.property
                        SealedInfo.Variant.Wrapped.Pointer.Object -> ""
                    }
                    val right = "if${variant.uppercasedIdentifier}($argument)"

                    "\t\t$left -> $right"
                }
            }
        )
        .build()
}
