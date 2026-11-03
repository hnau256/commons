package org.hnau.commons.gen.pipe.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import org.hnau.commons.gen.kotlin.processDeferrableSymbols
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
        val result = processDeferrableSymbols(
            resolver = resolver,
            annotationName = PipeAnnotationClassInfo.nameWithPackage,
            create = { annotated ->
                InterfaceToImplement.create(
                    annotated = annotated,
                )
            },
        )
        val deferredDeclarations = result.deferred
            .filterIsInstance<KSClassDeclaration>()
            .toSet()
        val unresolved = result.values.implement(
            codeGenerator = codeGenerator,
            resolver = resolver,
            logger = logger,
            deferredDeclarations = deferredDeclarations,
        )
        return result.deferred + unresolved.map { it.declaration }
    }
}
