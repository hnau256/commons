package org.hnau.commons.gen.loggable.processor.loggableinfo

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import org.hnau.commons.kotlin.ifTrue

internal fun LoggableInfo.generateCode(codeGenerator: CodeGenerator) {

    val initializerVarName = "${className}LogInitializer"

    val staticLoggerInitializerProperty = PropertySpec
        .builder(
            name = initializerVarName,
            type = initializerClassName.parameterizedBy(loggerClassName),
            modifiers = listOf(KModifier.PRIVATE),
        )
        .initializer("%T()", initializerClassName)
        .build()

    val loggerProperty = PropertySpec
        .builder(
            name = "logger",
            type = loggerClassName,
            modifiers = listOf(KModifier.INTERNAL),
        )
        .receiver(
            ClassName(
                packageName = classPackage,
                simpleNames =
                    listOfNotNull(
                        className,
                        hasCompanion.ifTrue { "Companion" },
                    ),
            )
        )
        .getter(
            FunSpec
                .getterBuilder()
                .addStatement(
                    "return %N { %T.withTag(%S) }",
                    initializerVarName,
                    loggerClassName,
                    tag,
                )
                .build()
        )
        .build()

    val fileName = "${className}Logger"

    val fileSpec = FileSpec
        .builder(classPackage, fileName)
        .addProperty(staticLoggerInitializerProperty)
        .addProperty(loggerProperty)
        .build()

    codeGenerator
        .createNewFile(
            dependencies = Dependencies(
                aggregating = false,
                sources = listOfNotNull(loggableClassDeclaration.containingFile).toTypedArray(),
            ),
            packageName = classPackage,
            fileName = fileName,
        )
        .bufferedWriter()
        .use(fileSpec::writeTo)
}

private val loggerClassName: ClassName =
    ClassName("co.touchlab.kermit", "Logger")

private val initializerClassName: ClassName =
    ClassName("org.hnau.commons.kotlin.lazy", "Initializer")
