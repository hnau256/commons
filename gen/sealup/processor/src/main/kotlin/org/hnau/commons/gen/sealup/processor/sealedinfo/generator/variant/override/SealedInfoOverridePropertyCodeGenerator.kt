package org.hnau.commons.gen.sealup.processor.sealedinfo.generator.variant.override

import com.google.devtools.ksp.symbol.KSAnnotation
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toKModifier
import com.squareup.kotlinpoet.ksp.toTypeName
import org.hnau.commons.gen.kotlin.codeBlock
import org.hnau.commons.gen.sealup.processor.sealedinfo.SealedInfo
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.SealInfoCodeGeneratorConstants
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.use
import org.hnau.commons.kotlin.foldNullable

fun SealedInfo.Override.createPropertySpec(
    wrapped: SealedInfo.Variant.Wrapped,
    type: SealedInfo.Override.Type.Property,
    typeParamResolver: TypeParameterResolver,
    typeVariables: List<TypeVariableName>,
): PropertySpec {
    val typeName = result.toTypeName(typeParamResolver)
    return PropertySpec
        .builder(
            name = name,
            type = typeName,
        ).apply {
            modifiers += KModifier.OVERRIDE
            visibility.toKModifier()?.let { modifiers += it }
            this.typeVariables.addAll(typeVariables)
            annotations.addAll(this@createPropertySpec.annotations.map(KSAnnotation::toAnnotationSpec))
            mutable(type.mutable)

            receiver
                ?.toTypeName(typeParamResolver)
                ?.let(::receiver)

            getter(
                FunSpec
                    .getterBuilder()
                    .addCode(
                        codeBlock {
                            receiver.foldNullable(
                                ifNull = { "return ${use(wrapped)}.$name" },
                                ifNotNull = { "return with(${use(wrapped)}) { $name }" },
                            )
                        }
                    )
                    .build()
            )

            if (type.mutable) {
                setter(
                    FunSpec
                        .setterBuilder()
                        .addParameter("newValue", typeName)
                        .addCode(
                         codeBlock {
                             receiver.foldNullable(
                                 ifNull = { "${use(wrapped)}.$name = ${SealInfoCodeGeneratorConstants.setterParameterName}" },
                                 ifNotNull = { "with(${use(wrapped)}) { $name = ${SealInfoCodeGeneratorConstants.setterParameterName} }" },
                             )
                         }
                        ).build(),
                )
            }
        }.build()
}
