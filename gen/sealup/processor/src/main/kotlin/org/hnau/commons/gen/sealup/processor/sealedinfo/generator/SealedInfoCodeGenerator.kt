package org.hnau.commons.gen.sealup.processor.sealedinfo.generator

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.hnau.commons.gen.sealup.processor.sealedinfo.SealedInfo
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.SealInfoCodeGeneratorConstants
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.className
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.parentClassName
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.visibility
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.variant.toTypeSpec


fun SealedInfo.toTypeSpec(): TypeSpec = TypeSpec
    .interfaceBuilder(className)
    .apply {
        modifiers += KModifier.SEALED
        visibility?.let { modifiers += it }
        addSuperinterface(parentClassName)

        if (ordinal) {
            addSuperinterface(
                SealInfoCodeGeneratorConstants.comparableClassName.parameterizedBy(className),
            )

            funSpecs +=
                FunSpec
                    .builder(SealInfoCodeGeneratorConstants.compareToFunctionName)
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(
                        ParameterSpec
                            .builder(
                                name = SealInfoCodeGeneratorConstants.compareToOtherParameterName,
                                type = className,
                            ).build(),
                    ).returns(SealInfoCodeGeneratorConstants.intClassName)
                    .addStatement(
                        "return ${SealInfoCodeGeneratorConstants.ordinalPropertyName} - ${SealInfoCodeGeneratorConstants.compareToOtherParameterName}.${SealInfoCodeGeneratorConstants.ordinalPropertyName}",
                    ).build()
        }

        if (serializable) {
            annotations += AnnotationSpec
                .builder(SealInfoCodeGeneratorConstants.serializableClassName)
                .build()
        }

        if (ordinal) {
            propertySpecs += PropertySpec
                .builder(
                    SealInfoCodeGeneratorConstants.ordinalPropertyName,
                    SealInfoCodeGeneratorConstants.intClassName,
                )
                .build()
        }

        if (name) {
            propertySpecs += PropertySpec
                .builder(
                    SealInfoCodeGeneratorConstants.namePropertyName,
                    SealInfoCodeGeneratorConstants.stringClassName,
                )
                .build()
        }

        addTypes(
            variants.mapIndexed { index, variant ->
                variant.toTypeSpec(
                    index = index,
                    info = this@toTypeSpec,
                )
            }
        )
    }
    .build()