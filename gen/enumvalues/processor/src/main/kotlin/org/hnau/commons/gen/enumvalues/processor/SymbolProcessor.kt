package org.hnau.commons.gen.enumvalues.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import org.hnau.commons.gen.enumvalues.processor.info.EnumValuesInfo
import org.hnau.commons.gen.enumvalues.processor.info.builder.create
import org.hnau.commons.gen.enumvalues.processor.info.generator.generateCode
import org.hnau.commons.gen.kotlin.processDeferrableSymbols

class SymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    override fun process(
        resolver: Resolver,
    ): List<KSAnnotated> = processDeferrableSymbols(
        resolver = resolver,
        annotationName = AnnotationInfo.nameWithPackage,
        create = { annotated ->
            EnumValuesInfo.create(
                logger = logger,
                annotated = annotated,
            )
        },
        generate = { enumValuesInfo ->
            enumValuesInfo.generateCode(
                codeGenerator = codeGenerator,
            )
        },
    )
}
