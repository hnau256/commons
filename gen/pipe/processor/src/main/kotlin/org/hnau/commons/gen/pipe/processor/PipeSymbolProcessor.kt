package org.hnau.commons.gen.pipe.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.validate
import org.hnau.commons.kotlin.ifFalse
import org.hnau.commons.gen.pipe.processor.impl.InterfaceToImplement
import org.hnau.commons.gen.pipe.processor.impl.create
import org.hnau.commons.gen.pipe.processor.impl.implement
import org.hnau.commons.gen.pipe.processor.utils.PipeAnnotationClassInfo

class PipeSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    override fun process(
        resolver: Resolver,
    ): List<KSAnnotated> {

        resolver
            .getSymbolsWithAnnotation(PipeAnnotationClassInfo.nameWithPackage)
            .onEach { annotated ->
                annotated
                    .validate()
                    .ifFalse {
                        logger.warn("${annotated.location} is not valid")
                    }
            }
            .map { annotated ->
                InterfaceToImplement.create(
                    annotated = annotated,
                )
            }
            .toList()
            .implement(
                codeGenerator = codeGenerator,
                resolver = resolver,
                logger = logger,
            )

        return emptyList()
    }
}