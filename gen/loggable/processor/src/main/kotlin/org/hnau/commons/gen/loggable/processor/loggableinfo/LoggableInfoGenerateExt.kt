package org.hnau.commons.gen.loggable.processor.loggableinfo

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import org.hnau.commons.kotlin.ifTrue

internal fun LoggableInfo.generateCode(
    codeGenerator: CodeGenerator,
) {

    val getter = FunSpec
        .getterBuilder()
        .addStatement(
            "return %M.withTag(%S)",
            MemberName("co.touchlab.kermit", "Logger"),
            tag,
        )
        .build()

    val property = PropertySpec
        .builder(
            name = "log",
            type = ClassName("co.touchlab.kermit", "Logger"),
            modifiers = listOf(KModifier.INTERNAL),
        )
        .receiver(
            ClassName(
                packageName = classPackage,
                simpleNames = listOfNotNull(
                    className,
                    hasCompanion.ifTrue { "Companion" },
                ),
            )
        )
        .getter(getter)
        .build()

    val fileName = "${className}Logger"

    val fileSpec = FileSpec
        .builder(classPackage, fileName)
        .addProperty(property)
        .build()

    codeGenerator
        .createNewFile(
            dependencies = Dependencies(false),
            packageName = classPackage,
            fileName = fileName,
        )
        .bufferedWriter()
        .use(fileSpec::writeTo)
}