package org.hnau.commons.gen.sealup.processor.sealedinfo.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.FileSpec
import org.hnau.commons.gen.sealup.processor.sealedinfo.SealedInfo
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.packageName

fun SealedInfo.generateCode(
    codeGenerator: CodeGenerator,
) {

    val file = FileSpec
        .builder(packageName, sealedInterfaceName)
        .apply {
            addType(
                toTypeSpec()
            )
            if (fold) {
                addFunction(
                    toFoldFuncSpec()
                )
            }
            factoryMethods?.let { parentExtension ->
                toFactoriesSpec(
                    parentExtension = parentExtension,
                ).forEach { factory ->
                    factory.fold(
                        ifLeft = ::addProperty,
                        ifRight = ::addFunction,
                    )
                }
            }
        }
        .build()

    codeGenerator
        .createNewFile(
            dependencies = Dependencies(
                aggregating = false,
                sources = buildList {
                    add(parent.containingFile)
                    addAll(
                        variants.mapNotNull { variant ->
                            variant.wrapped?.type?.declaration?.containingFile
                        }
                    )
                }
                    .filterNotNull()
                    .toTypedArray(),
            ),
            packageName = packageName,
            fileName = sealedInterfaceName,
        )
        .use { out ->
            out
                .writer()
                .use { writer -> file.writeTo(writer) }
        }
}