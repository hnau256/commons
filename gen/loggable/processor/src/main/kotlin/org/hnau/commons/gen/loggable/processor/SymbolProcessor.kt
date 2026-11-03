package org.hnau.commons.gen.loggable.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import org.hnau.commons.gen.kotlin.processDeferrableSymbols
import org.hnau.commons.gen.loggable.processor.loggableinfo.LoggableInfo
import org.hnau.commons.gen.loggable.processor.loggableinfo.create
import org.hnau.commons.gen.loggable.processor.loggableinfo.generateCode
import org.hnau.commons.kotlin.castOrNull

internal class SymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    override fun process(
        resolver: Resolver,
    ): List<KSAnnotated> = processDeferrableSymbols(
        resolver = resolver,
        annotationName = AnnotationInfo.nameWithPackage,
        create = { annotated ->
            annotated
                .castOrNull<KSClassDeclaration>()
                ?.let(LoggableInfo.Companion::create)
                ?: run {
                    logger.error(
                        message = "@${AnnotationInfo.simpleName} can only be applied to classes",
                        symbol = annotated,
                    )
                    null
                }
        },
        generate = { loggableInfo ->
            loggableInfo.generateCode(codeGenerator)
        },
    ).deferred
}
