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
        val toImplement = mutableListOf<InterfaceToImplement>()
        val deferred = processDeferrableSymbols(
            resolver = resolver,
            annotationName = PipeAnnotationClassInfo.nameWithPackage,
            create = { annotated ->
                InterfaceToImplement.create(
                    annotated = annotated,
                )
            },
            generate = { interfaceToImplement ->
                toImplement += interfaceToImplement
            },
        )
        val deferredDeclarations = deferred
            .mapNotNull { it as? KSClassDeclaration }
            .toSet()
        val unresolved = toImplement.implement(
            codeGenerator = codeGenerator,
            resolver = resolver,
            logger = logger,
            deferredDeclarations = deferredDeclarations,
        )
        return deferred + unresolved.map { it.declaration }
    }
}
