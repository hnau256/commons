package org.hnau.commons.gen.sealup.processor.sealedinfo.generator

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.TypeVariableName
import org.hnau.commons.gen.kotlin.codeBlock
import org.hnau.commons.gen.sealup.processor.sealedinfo.SealedInfo
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.className
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.fold
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.uppercasedIdentifier
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.visibility
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.wrapperClassName

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
                                .wrapped
                                ?.pointer
                                ?.fold(
                                    ifClass = { variant.wrapped.className },
                                    ifObject = { null },
                                ),
                        ).toTypedArray(),
                        returnType = resultType,
                    ),
                )
            }
        }
        .addCode(
            codeBlock {
                variants.joinToString(
                    prefix = "return when (this) {\n",
                    postfix = "\n\t}",
                    separator = "\n",
                ) { variant ->

                    val wrapper = variant.wrapperClassName(this@toFoldFuncSpec)
                    val prefix = variant
                        .wrapped
                        ?.pointer
                        ?.fold(
                            ifClass = { "is " },
                            ifObject = { null }
                        )
                        .orEmpty()
                    val left = "$prefix${use(wrapper)}"

                    val argument = variant
                        .wrapped
                        ?.pointer
                        ?.fold(
                            ifClass = SealedInfo.Variant.Wrapped.Pointer.Class::property,
                            ifObject = { null }
                        )
                        .orEmpty()
                    val right = "if${variant.uppercasedIdentifier}($argument)"

                    "\t\t$left -> $right"
                }
            }
        )
        .build()
}
