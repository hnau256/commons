package org.hnau.commons.gen.sealup.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import org.hnau.commons.gen.sealup.processor.sealedinfo.CreateResult
import org.hnau.commons.gen.sealup.processor.sealedinfo.SealedInfo
import org.hnau.commons.gen.sealup.processor.sealedinfo.builder.create
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.generateCode

class SymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    private data class RoundState(
        val deferred: List<KSAnnotated> = emptyList(),
        val successCount: Int = 0,
    )

    override fun process(
        resolver: Resolver,
    ): List<KSAnnotated> {
        val state = resolver
            .getSymbolsWithAnnotation(AnnotationInfo.nameWithPackage)
            .fold(RoundState()) { state, annotated ->
                when (val result = SealedInfo.create(logger, annotated)) {
                    is CreateResult.Success -> {
                        result.value.generateCode(codeGenerator)
                        state.copy(successCount = state.successCount + 1)
                    }

                    is CreateResult.Deferred -> {
                        state.copy(deferred = state.deferred + annotated)
                    }

                    is CreateResult.Error -> state
                }
            }

        if (state.deferred.isNotEmpty() && state.successCount == 0) {
            val names = state.deferred.mapNotNull { annotated ->
                (annotated as? KSClassDeclaration)?.qualifiedName?.asString()
            }
            logger.error(
                "Unable to resolve @${AnnotationInfo.simpleName} dependencies. " +
                    "The following types have unresolved variant types: $names"
            )
            return emptyList()
        }

        return state.deferred
    }
}