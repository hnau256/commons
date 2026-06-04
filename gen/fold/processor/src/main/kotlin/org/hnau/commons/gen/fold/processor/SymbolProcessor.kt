package org.hnau.commons.gen.fold.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import org.hnau.commons.gen.fold.processor.info.FoldInfo
import org.hnau.commons.gen.fold.processor.info.builder.create
import org.hnau.commons.gen.fold.processor.info.generator.generateCode

class SymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    override fun process(
        resolver: Resolver,
    ): List<KSAnnotated> {

        resolver
            .getSymbolsWithAnnotation(AnnotationInfo.nameWithPackage)
            .mapNotNull { annotated ->
                FoldInfo.create(
                    logger = logger,
                    annotated = annotated,
                )
            }
            .forEach { foldInfo ->
                foldInfo.generateCode(
                    codeGenerator = codeGenerator,
                )
            }

        return emptyList()
    }
}
