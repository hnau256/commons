package org.hnau.commons.gen.loggable.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import org.hnau.commons.gen.loggable.processor.loggableinfo.LoggableInfo
import org.hnau.commons.gen.loggable.processor.loggableinfo.create
import org.hnau.commons.gen.loggable.processor.loggableinfo.generateCode
import org.hnau.commons.kotlin.castOrNull
import org.hnau.commons.kotlin.ifNull

internal class SymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    override fun process(
        resolver: Resolver,
    ): List<KSAnnotated> {

        resolver
            .getSymbolsWithAnnotation(AnnotationInfo.nameWithPackage)
            .mapNotNull { annotated ->
                annotated
                    .castOrNull<KSClassDeclaration>()
                    .ifNull {
                        logger.error(
                            message = "@${AnnotationInfo.simpleName} can only be applied to classes",
                            symbol = annotated,
                        )
                        null
                    }
            }
            .map(LoggableInfo.Companion::create)
            .forEach { loggableInfo ->
                loggableInfo.generateCode(codeGenerator)
            }

        return emptyList()
    }
}
