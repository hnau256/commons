package org.hnau.commons.gen.sealup.processor.sealedinfo.generator.variant

import arrow.core.toNonEmptyListOrNull
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.hnau.commons.gen.sealup.processor.sealedinfo.SealedInfo
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.SealInfoCodeGeneratorConstants
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.className
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.fold
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.variant.override.createSpec
import org.hnau.commons.kotlin.ifNull
import org.hnau.commons.kotlin.it

fun SealedInfo.Variant.toTypeSpec(
    index: Int,
    info: SealedInfo,
): TypeSpec = wrapped
    ?.pointer
    ?.fold(
        ifClass = { TypeSpec.classBuilder(wrapperClass) },
        ifObject = { null },
    )
    .ifNull { TypeSpec.objectBuilder(wrapperClass) }
    .apply {
        modifiers += KModifier.DATA
        addSuperinterface(info.className)

        if (info.serializable) {
            annotations += AnnotationSpec
                .builder(SealInfoCodeGeneratorConstants.serializableClassName)
                .build()
            annotations += AnnotationSpec
                .builder(SealInfoCodeGeneratorConstants.serialNameClassName)
                .addMember("\"$serialName\"")
                .build()
        }

        if (info.ordinal) {
            propertySpecs += PropertySpec
                .builder(
                    SealInfoCodeGeneratorConstants.ordinalPropertyName,
                    SealInfoCodeGeneratorConstants.intClassName,
                )
                .addModifiers(KModifier.OVERRIDE)
                .getter(
                    FunSpec
                        .getterBuilder()
                        .addStatement("return $index")
                        .build(),
                )
                .build()
        }

        if (info.name) {
            propertySpecs += PropertySpec
                .builder(
                    SealInfoCodeGeneratorConstants.namePropertyName,
                    SealInfoCodeGeneratorConstants.stringClassName,
                )
                .addModifiers(KModifier.OVERRIDE)
                .getter(
                    FunSpec
                        .getterBuilder()
                        .addStatement("return \"$wrapperIdentifier\"")
                        .build(),
                )
                .build()
        }

        wrapped
            ?.pointer
            ?.fold(
                ifObject = { null },
                ifClass = ::it,
            )
            ?.let { classPointer ->
                primaryConstructor(
                    FunSpec
                        .constructorBuilder()
                        .addParameter(
                            ParameterSpec
                                .builder(
                                    name = classPointer.property,
                                    type = wrapped.className,
                                )
                                .build(),
                        )
                        .build(),
                )

                propertySpecs += PropertySpec
                    .builder(
                        name = classPointer.property,
                        type = wrapped.className,
                    )
                    .initializer(classPointer.property)
                    .build()
            }

        info
            .overrides
            .toNonEmptyListOrNull()
            ?.let { overrides ->

                val wrapped = this@toTypeSpec
                    .wrapped
                    ?: error("Unable create overrides for variant without wrapped class or object. Variant: $wrapperIdentifier")

                overrides.forEach { override ->
                    override
                        .createSpec(
                            wrapped = wrapped,
                        )
                        .fold(
                            ifLeft = ::addFunction,
                            ifRight = ::addProperty,
                        )
                }
            }


    }
    .build()
