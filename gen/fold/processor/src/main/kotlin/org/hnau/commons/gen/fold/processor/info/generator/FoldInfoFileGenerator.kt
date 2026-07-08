package org.hnau.commons.gen.fold.processor.info.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.FileSpec
import org.hnau.commons.gen.fold.processor.info.FoldInfo
import org.hnau.commons.gen.fold.processor.info.generator.utils.fileName
import org.hnau.commons.gen.fold.processor.info.generator.utils.packageName

fun FoldInfo.generateCode(
    codeGenerator: CodeGenerator,
) {

    val file = FileSpec
        .builder(packageName, fileName)
        .apply {
            addFunction(toFoldRawFunSpec())
            addFunction(toFoldFunSpec())
        }
        .build()

    codeGenerator
        .createNewFile(
            dependencies = Dependencies(
                aggregating = false,
                sources = listOfNotNull(classDeclaration.containingFile).toTypedArray(),
            ),
            packageName = packageName,
            fileName = fileName,
        )
        .use { out ->
            out
                .writer()
                .use { writer -> file.writeTo(writer) }
        }
}
