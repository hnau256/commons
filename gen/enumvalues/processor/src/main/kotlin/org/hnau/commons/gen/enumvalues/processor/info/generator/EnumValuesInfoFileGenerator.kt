package org.hnau.commons.gen.enumvalues.processor.info.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.FileSpec
import org.hnau.commons.gen.enumvalues.processor.info.EnumValuesInfo
import org.hnau.commons.gen.enumvalues.processor.info.generator.utils.packageName

fun EnumValuesInfo.generateCode(
    codeGenerator: CodeGenerator,
) {

    val file = FileSpec
        .builder(packageName, valuesClass)
        .apply {
            addType(
                toTypeSpec()
            )
        }
        .build()

    codeGenerator
        .createNewFile(
            dependencies = Dependencies(
                aggregating = false,
                sources = listOfNotNull(enumClass.containingFile).toTypedArray(),
            ),
            packageName = packageName,
            fileName = valuesClass,
        )
        .use { out ->
            out
                .writer()
                .use { writer -> file.writeTo(writer) }
        }
}